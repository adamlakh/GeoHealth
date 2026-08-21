import {Component, AfterViewInit, Inject, PLATFORM_ID, signal, ChangeDetectorRef, computed} from '@angular/core';
import {CommonModule, isPlatformBrowser} from '@angular/common';
import {RouterModule, ActivatedRoute} from '@angular/router';
import { FinalMapService } from '../../core/service/MapService/FinalMapService/finalMapService';
import { RasterMapService } from '../../core/service/MapService/RasterService/RasterMapService';
import { RasterMapListDto } from '../../shared/models/MapModel/RasterMapModel/RasterMapListDto';
import {ResponseEvaluationFormDto} from '../../shared/models/EvaluationFormModel/ResponseEvaluationFormDto';
import {EvaluationFormService} from '../../core/service/EvaluationFormService/EvaluationFormService';
import {UsersServices} from '../../core/service/UserService/users-services';
import {AdminEvaluationFormService} from '../../core/service/AdminService/AdminEvaluationFormService/AdminEvaluationFormService';
import {AdminResponseEvaluationFormDto} from '../../shared/models/AdminModel/EvaluationFormModel/AdminResponseEvaluationFormDto';
import { MapLayerHelper } from './map-layer-helper';
import { TranslocoService } from '@jsverse/transloco';
import { RISK_LEVELS, getRiskColor, normalizeRasterTitle } from './map-utils';
import {CAMEROON_COORDINATES} from './map.constants';
import {CAMEROON_ZOOM} from './map.constants';
import {AnnotationService} from '../../core/service/MapService/AnnotationService/AnnotationService';
import {AnnotationDTO} from '../../shared/models/MapModel/AnnotationModel/AnnotationDTO';
import {MapMetrics} from './map.metrics';
import {EvaluatorAgreementMeasureService} from '../../core/service/MeasureService/EvaluatorAgreementMeasureService/evaluatorAgreementMeasureService';
import {MeanMeasureService} from '../../core/service/MeasureService/MeanMeasureService/meanMeasureService';
import {DivisionRiskDto} from '../../shared/models/MeasureModel/DivisionRiskDto';
import {TranslocoPipe} from '@jsverse/transloco';
import {MapLegendComponent} from './map-legend/map-legend';
import {TooltipDescriptionComponent} from '../../shared/components/tooltip-description/tooltip-description';
import {ButtonComponent} from '../../shared/components/button.component/button.component';
import {EvaluationModalComponent} from './evaluation-modal/evaluation-modal';
import {EvaluationCommentComponent} from './evaluation-comment/evaluation-comment';
import {
  ModelEvaluationMeasureService
} from '../../core/service/MeasureService/ModelEvaluationMeasureService/modelEvaluationMeasureService';
import {FormsModule} from '@angular/forms';
import {ReportService} from '../../core/service/ReportService/reportService';
import {EvaluatorsModalComponent} from './evaluators-modal/evaluators-modal';
import {UserSummaryDto} from '../../shared/models/AdminModel/UserModel/UserSummaryDto';

@Component({
  selector: 'app-map',
  imports: [RouterModule, CommonModule, TranslocoPipe, MapLegendComponent, TooltipDescriptionComponent, ButtonComponent, EvaluationModalComponent, EvaluatorsModalComponent, EvaluationCommentComponent, FormsModule],
  templateUrl: './map.component.html',
  styleUrl: './map.component.css',
  standalone: true,
})
export class MapComponent implements AfterViewInit {

  // risk levels used to display the map legend
  riskLevels = RISK_LEVELS;
  // id of the current shapefile map loaded from the route
  mapId: number = -1;
  isAdmin:boolean=false;
  selectedDivision = signal<any>(null);
  mapTitle = signal<string>('');
  mapTag = signal<string>('');
  mapDescription = signal<string>('');
  // the raster layer linked to this specific map, used in the dropdown
  rasterMaps = signal<RasterMapListDto[]>([]);

  showEvaluationModal = signal<boolean>(false);
  // the existing evaluation form for the selected division
  existingForm = signal<ResponseEvaluationFormDto | null>(null);
  allEvaluationFormsUser= signal<ResponseEvaluationFormDto[]>([]);
  allEvaluationFormsAdmin= signal<AdminResponseEvaluationFormDto[]>([]);
  allDivisions = signal<{ name: string, risk: string}[]>([]);
  searchValue: string = '';

  // Help computing the metrics for the map
  public mapMetrics!: MapMetrics;

  // helper class managing the Leaflet map layers and interactions
  public mapHelper = new MapLayerHelper();

  showEvaluatorsModal = signal<boolean>(false);

  inspectModeActive : boolean = false;

  // about annotations
  currentUserId = -1;
  saveMessage = '';
  isSaving = false;
  private lastDivisionName: string | null = null;

  constructor(
    @Inject(PLATFORM_ID) private platformId: Object,
    private mapService: FinalMapService,
    private usersServices: UsersServices,
    private rasterMapService: RasterMapService,
    private evaluationFormService: EvaluationFormService,
    private adminEvaluationFormService:AdminEvaluationFormService,
    private cdr: ChangeDetectorRef,
    private route: ActivatedRoute,
    private annotationService: AnnotationService,
    private evaluatorAgreementMeasureService:EvaluatorAgreementMeasureService,
    private meanMeasureService: MeanMeasureService,
    private modelEvaluationMeasureService: ModelEvaluationMeasureService,
    private reportService: ReportService,
    private translocoService: TranslocoService
    ){
   this.mapMetrics= new MapMetrics(
        this.evaluatorAgreementMeasureService,
        this.meanMeasureService,
        this.modelEvaluationMeasureService)
  }

  /**
   * Display the map OSM thanks to Leaflet on Cameron and load the evaluation forms
   */
  async ngAfterViewInit(): Promise<void> {
    if (!isPlatformBrowser(this.platformId)) return;
    this.mapId = Number(this.route.snapshot.paramMap.get('id'));
    this.loadUserRole();
    await this.mapHelper.initMap('map', CAMEROON_COORDINATES[0], CAMEROON_ZOOM, 6, 12, true);
    this.loadBaseMap();
  }


  // --- Map Loading ---

  /**
   * Fetches the shapefile map data from the backend, draws the divisions layer
   * and loads the raster layer linked to this map into the dropdown
   */
  private loadBaseMap(): void {
    this.mapService.getMap(this.mapId).subscribe({
      next: (mapData) => {
        this.mapTitle.set(mapData.title);
        this.mapDescription.set(mapData.description);
        this.rasterMaps.set(mapData.rasterMaps ?? []);
        this.mapTag.set(mapData.tags);

        const geoJson = JSON.parse(mapData.fileGeoJson);
        const divisions: { name: string, risk: string }[] = [];

        for (const feature of geoJson.features) {
          divisions.push({
            name: feature.properties.NAME_2,
            risk: feature.properties.rsk_cls
          });
        }
        this.allDivisions.set(divisions);
        this.loadMeasurements('', '');
        this.mapHelper.applyDivisionsLayer(
          mapData.fileGeoJson,
          (event) => {
              this.onDivisionClicked(event);
              },
          this.mapTag().at(0));
      },
      error: (err) => console.error('Failed to load map data', err)
    });
  }

  /**
   * Checks if the connected user is an admin
   * and loads the appropriate evaluation forms accordingly
   */
  private loadUserRole(): void {
    this.usersServices.isAdmin().subscribe(
      bool =>{
        this.isAdmin=bool;
        this.getAllForm(bool,this.mapId)
      });
  }

  // --- Map Interaction ---

  /**
   * Returns the translated label for a raw raster title (e.g. "Deforestation.tif"),
   * falling back to the cleaned-up raw title if no translation entry exists.
   */
  getRasterLabel(rawTitle: string): string {
    const cleanTitle = normalizeRasterTitle(rawTitle);
    const key = 'rasterNames.' + cleanTitle;
    const translated = this.translocoService.translate(key);
    return translated === key ? cleanTitle : translated;
  }

  /**
   * Method called when the user selects a risk factor map
   * (or division only by default) to adapt the map displayed
   *
   * @param event :
   *    - division only : if no risk factors were selected or division only selected
   *    - "name of risk factor map" : otherwise
   * */
  onMapSelected(event: Event): void {
      const value: string = (event.target as HTMLSelectElement).value;
      const placeholder = this.translocoService.translate('map.dropdown');

      if (!value || value === placeholder) {
          this.mapHelper.switchTo({ id: null, kind: 'divisions', title: 'Risk Overview - Divisions' });
          return;
        }

        const found = this.rasterMaps().find(
          map => map.id === Number(value) || this.getRasterLabel(map.title) === value
        );

        if (found) {
          this.mapHelper.switchTo({ id: found.id, kind: 'raster', title: '' });
          return;
        }

        this.mapHelper.switchTo({ id: null, kind: 'divisions', title: 'Risk Overview - Divisions' });
      }

  /**
   * Handles a click on a division.
   * If the division is already selected, deselects it and clears the marker.
   * Otherwise, selects it, places a marker, loads the existing evaluation form
   * and computes the risk measurements for that division.
   *
   * @param event - the click event containing:
   *   - properties: the GeoJSON properties of the clicked division (name, risk category, etc.)
   *   - latlng: the coordinates of the click on the map
   */
  private onDivisionClicked(event: { properties: any, latlng: any }): void {

    if ((this.mapHelper.getMap() as any).pm.globalDrawModeEnabled()) {
      return;
    }

    if (!event.properties || !event.properties.NAME_2) {
      return;
    }

    if (!this.mapHelper.isRasterActive() && this.selectedDivision() === event.properties) {
      this.selectedDivision.set(null);
      this.existingForm.set(null);
      this.loadMeasurements('', '');
      this.mapHelper.clearMarker();
      return;
    }
    this.saveMessage= '';

    // delete annotation if the division selected is not the same that the previous
    if (this.lastDivisionName !== event.properties.NAME_2) {
      this.mapHelper.clearGeomanLayers();
      this.lastDivisionName = event.properties.NAME_2;
    }

    this.selectedDivision.set(event.properties);
    if (!this.mapHelper.isRasterActive()) {
        this.mapHelper.placeMarker(event.latlng);
    }

    this.evaluationFormService.getMyFormForADiv(this.mapId, event.properties.NAME_2).subscribe({
      next: (form) => this.existingForm.set(form),
      error: () => this.existingForm.set(null)
    });

    this.usersServices.getUserForAnnotation().subscribe({
      next: userInfo => {
        this.currentUserId = userInfo.id

        this.annotationService.getAnnotations(this.mapId,this.currentUserId, event.properties.dvsn_nm ?? event.properties.NAME_2).subscribe({
          next: (data) => {

            if (data?.geoJson) {
              this.mapHelper.loadAnnotationsFromGeoJson(data.geoJson.toString());
              this.cdr.detectChanges();
            }
          },
          error: (err) => {
              console.error('Unexpected annotation error:', err);
          }
        });
      }
    });
    this.loadMeasurements(event.properties.NAME_2, event.properties.rsk_cls);
  }

  /**
   * Loads all risk measurements for the selected division
   *
   * @param divisionName - the name of the selected division (NAME_2 in GeoJSON)
   * @param riskCategory - the risk category of the selected division (Risk_categ in GeoJSON)
   */
  private loadMeasurements(divisionName: string, riskCategory: string): void {

    const divisionRiskDto: DivisionRiskDto = {
      divisionRiskLevel: Object.fromEntries(
        this.allDivisions().map(d => [d.name, d.risk])
      )
    };

    this.mapMetrics.computeAllMetrics(this.mapId,divisionName,riskCategory,divisionRiskDto);

  }

  // --- Evaluation ---

  /**
   * Get the evaluation form for a specific map
   *
   * @param isAdmin true if the connected user is an admin, false otherwise
   * @param mapId the id of the map you are interested in
   */
  private getAllForm(isAdmin:boolean,mapId:number){
    if(isAdmin){
      this.adminEvaluationFormService.getAllForm(mapId).subscribe({
        next: (evaluationForms:AdminResponseEvaluationFormDto[])=>{
          this.allEvaluationFormsAdmin.set(evaluationForms);
        },
        error: (err)=>{
          console.error('Failed to load evaluation forms', err);
        }
      })
    }
    else{
      this.evaluationFormService.getAllForm(mapId).subscribe({
        next: (evaluationForms:ResponseEvaluationFormDto[])=>{
          this.allEvaluationFormsUser.set(evaluationForms);
        },
        error: (err)=>{
          console.error('Failed to load evaluation forms', err);
        }
      })
    }
  }

  /**
   * Opens the evaluation modal for the selected division
   */
  onOpenEvaluation(): void {
    this.showEvaluationModal.set(true);
  }

  /**
   * Closes the evaluation modal and reloads the evaluation forms
   */
  onCloseEvaluation(): void {
    this.showEvaluationModal.set(false);
    this.getAllForm(this.isAdmin,this.mapId);
    this.loadMeasurements(this.selectedDivision().NAME_2, this.selectedDivision().rsk_cls);
    this.cdr.detectChanges();
  }

  /**
   * Deletes the current evaluation form after user confirmation
   */
  onDeleteEvaluation(): void {
    if (!confirm('Are you sure you want to delete this evaluation?')) return;

    this.evaluationFormService.deleteForm(this.existingForm()!.id).subscribe({
      next: () => {
        this.existingForm.set(null);
        this.getAllForm(this.isAdmin, this.mapId);
      },
      error: (err) => {
        console.error('Failed to delete evaluation form', err);
      }
    });
  }

  // --- Report ---

  /**
   *  Make a get request to download the xlsx report for the current map
   */
  onReportButtonClicked(): void {

    //Map of all division and their risks
    const divisionRiskDto: DivisionRiskDto = {
      divisionRiskLevel: Object.fromEntries(
        this.allDivisions().map(d => [d.name, d.risk ?? ""])
      )
    };

    // Get the report and download it
    this.reportService.getReport(this.mapId,divisionRiskDto).subscribe({
      next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const urlProxy = document.createElement('a');
          urlProxy.href = url;
          urlProxy.download = 'report.xlsx';
          urlProxy.click();
          window.URL.revokeObjectURL(url);
          console.log('Report successfully downloaded');
      },
      error: (err) => {
        console.error('Failed to generate report', err);
      }
    });

  }

  // --- Utilities ---

  /**
   * gives the color associated with the risk category
   *
   * @param riskClass - the risk category string (e.g. 'High', 'Low')
   * @returns the color string associated with the risk class
   */
  getRiskColor(riskClass: string): string {
    return getRiskColor(riskClass);
  }

  toggleInspectMode(): void {
    this.inspectModeActive = !this.inspectModeActive;
    this.mapHelper.toggleInspectMode(this.inspectModeActive);
  }


  // ------- about annotations -------
  public saveAnnotation() {
    const geojsonData = this.mapHelper.getGeomanGeojson();

    if (geojsonData == null) {
      this.saveMessage = 'No annotations here';
      return;
    }
    if (this.selectedDivision() == null) {
      this.saveMessage = 'No divisions selected';
      return;
    }

    this.isSaving = true;

    this.usersServices.getUserForAnnotation().subscribe({
      next: userInfo => {
        const dto: AnnotationDTO = {
          mapId: this.mapId,
          userId: userInfo.id,
          division: this.selectedDivision().dvsn_nm ?? this.selectedDivision().NAME_2,
          geoJson: geojsonData
        };

        this.annotationService.postAnnotations(dto).subscribe({
          next: () => {
            this.saveMessage = 'Annotations saved';
            this.isSaving = false;
            // Allow angular to update the state displayed of teh saveMessage
            this.cdr.detectChanges();
          },
          error: (err) => {
            this.saveMessage = 'Annotations cant be saved';
            this.isSaving = false;
            console.error(err);
            this.cdr.detectChanges();
          }
        });

      },
      error: (err) => {
        this.saveMessage = 'User not detected';
        this.isSaving = false;
        console.error(err);
      }
    });
  }

  onUserReportButtonClicked(): void {
    this.showEvaluatorsModal.set(true);
  }

  onEvaluatorSelected(user: UserSummaryDto): void {
    this.showEvaluatorsModal.set(false);

    this.reportService.getUserReport(this.mapId, user.id).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const urlProxy = document.createElement('a');
        urlProxy.href = url;
        urlProxy.download = `report-${user.username}-${this.mapTitle()}.xlsx`;
        urlProxy.click();
        window.URL.revokeObjectURL(url);
        console.log('User report successfully downloaded');
      },
      error: (err) => {
        console.error('Failed to generate user report', err);
      }
    });

    this.reportService.getUserProfile(user.id).subscribe({
      next: (blob: Blob) => {
        const url = window.URL.createObjectURL(blob);
        const urlProxy = document.createElement('a');
        urlProxy.href = url;
        urlProxy.download = `profile-${user.username}.xlsx`;
        urlProxy.click();
        window.URL.revokeObjectURL(url);
        console.log('User profile successfully downloaded');
      },
      error: (err) => {
        console.error('Failed to generate user profile', err);
      }
    });
  }

  onCloseEvaluatorsModal(): void {
    this.showEvaluatorsModal.set(false);
  }

}
