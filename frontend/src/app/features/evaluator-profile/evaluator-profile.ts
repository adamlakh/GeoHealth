import {Component, OnInit, ChangeDetectorRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {FormsModule} from '@angular/forms';
import {Router} from '@angular/router';
import {EvaluatorProfileService} from '../../core/service/EvaluatorProfileService/EvaluatorProfileService';
import {SaveEvaluatorProfileDto} from '../../shared/models/EvaluatorProfileModel/SaveEvaluatorProfileDto';
import {SaveDiseaseExperienceDto} from '../../shared/models/EvaluatorProfileModel/SaveDiseaseExperienceDto';
import { TranslocoService, TranslocoPipe  } from '@jsverse/transloco';
import { ResponseEvaluatorProfileDto } from '../../shared/models/EvaluatorProfileModel/ResponseEvaluatorProfileDto';

@Component({
  selector: 'app-evaluator-profile',
  standalone: true,
  imports: [CommonModule, FormsModule, TranslocoPipe],
  templateUrl: './evaluator-profile.html',
  styleUrl: './evaluator-profile.css',
})
export class EvaluatorProfile implements OnInit {
  selectedProfessions: string[] = [];
  selectedSectors: string[] = [];
  selectedInterventionLevels: string[] = [];
  selectedSectorsWorkedIn: string[] = [];
  otherProfession = '';
  otherSector = '';
  otherInterventionLevel = '';
  otherSectorWorkedIn = '';

  countries = '';
  regions = '';
  divisions = '';

  showValidationError = false;
  hasExistingProfile = false;
  isEditing = true;

  rvfExperience = {
    pathogenKnowledgeScore: null as number | null,
    transmissionKnowledgeScore: null as number | null,
    animalClinicalKnowledgeScore: null as number | null,
    humanClinicalKnowledgeScore: null as number | null,
    professionallyExposed: '',
    exposureFrequency: '',
    yearsInvolved: '',
  };

  evdExperience = {
    pathogenKnowledgeScore: null as number | null,
    transmissionKnowledgeScore: null as number | null,
    animalClinicalKnowledgeScore: null as number | null,
    humanClinicalKnowledgeScore: null as number | null,
    professionallyExposed: '',
    exposureFrequency: '',
    yearsInvolved: '',
  };

  constructor(
    private evaluatorProfileService: EvaluatorProfileService,
    private router: Router,
    private cdr: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.evaluatorProfileService.hasProfile().subscribe({
      next: (hasProfile) => {
        this.hasExistingProfile = hasProfile;
        this.isEditing = !hasProfile;
        if (hasProfile) {
          this.loadExistingProfile();
        }
      },
      error: (err) => console.error('Error while checking evaluator profile', err),
    });
  }

  enableEdit(): void {
    this.isEditing = true;
  }

  toggle(selected: string[], value: string, checked: boolean): void {
    const index = selected.indexOf(value);
    if (checked && index === -1) {
      selected.push(value);
    } else if (!checked && index !== -1) {
      selected.splice(index, 1);
    }
  }

  private isDiseaseExperienceValid(experience: {
    pathogenKnowledgeScore: number | null;
    transmissionKnowledgeScore: number | null;
    animalClinicalKnowledgeScore: number | null;
    humanClinicalKnowledgeScore: number | null;
    professionallyExposed: string;
    exposureFrequency: string;
    yearsInvolved: string;
  }): boolean {
    return (
      experience.pathogenKnowledgeScore !== null &&
      experience.transmissionKnowledgeScore !== null &&
      experience.animalClinicalKnowledgeScore !== null &&
      experience.humanClinicalKnowledgeScore !== null &&
      experience.professionallyExposed !== '' &&
      experience.exposureFrequency !== '' &&
      experience.yearsInvolved !== ''
    );
  }

  private isFormValid(): boolean {
    return (
      this.selectedProfessions.length > 0 &&
      this.selectedSectors.length > 0 &&
      this.selectedInterventionLevels.length > 0 &&
      this.selectedSectorsWorkedIn.length > 0 &&
      !!this.countries.trim() &&
      !!this.regions.trim() &&
      !!this.divisions.trim() &&
      this.isDiseaseExperienceValid(this.rvfExperience) &&
      this.isDiseaseExperienceValid(this.evdExperience)
    );
  }

  private toDiseaseExperienceDto(experience: {
    pathogenKnowledgeScore: number | null;
    transmissionKnowledgeScore: number | null;
    animalClinicalKnowledgeScore: number | null;
    humanClinicalKnowledgeScore: number | null;
    professionallyExposed: string;
    exposureFrequency: string;
    yearsInvolved: string;
  }): SaveDiseaseExperienceDto {
    return {
      pathogenKnowledgeScore: experience.pathogenKnowledgeScore as number,
      transmissionKnowledgeScore: experience.transmissionKnowledgeScore as number,
      animalClinicalKnowledgeScore: experience.animalClinicalKnowledgeScore as number,
      humanClinicalKnowledgeScore: experience.humanClinicalKnowledgeScore as number,
      professionallyExposed: experience.professionallyExposed === 'yes',
      exposureFrequency: experience.exposureFrequency,
      yearsInvolved: experience.yearsInvolved,
    };
  }

  private fromDiseaseExperienceDto(dto: SaveDiseaseExperienceDto): {
    pathogenKnowledgeScore: number | null;
    transmissionKnowledgeScore: number | null;
    animalClinicalKnowledgeScore: number | null;
    humanClinicalKnowledgeScore: number | null;
    professionallyExposed: string;
    exposureFrequency: string;
    yearsInvolved: string;
  } {
    return {
      pathogenKnowledgeScore: dto.pathogenKnowledgeScore,
      transmissionKnowledgeScore: dto.transmissionKnowledgeScore,
      animalClinicalKnowledgeScore: dto.animalClinicalKnowledgeScore,
      humanClinicalKnowledgeScore: dto.humanClinicalKnowledgeScore,
      professionallyExposed: dto.professionallyExposed ? 'yes' : 'no',
      exposureFrequency: dto.exposureFrequency,
      yearsInvolved: dto.yearsInvolved,
    };
  }

  submit(): void {
    if (!this.isFormValid()) {
      this.showValidationError = true;
      return;
    }

    this.showValidationError = false;

    const dto: SaveEvaluatorProfileDto = {
      professions: this.selectedProfessions,
      sectors: this.selectedSectors,
      interventionLevels: this.selectedInterventionLevels,
      sectorsWorkedIn: this.selectedSectorsWorkedIn,
      countries: this.countries.trim(),
      regions: this.regions.trim(),
      divisions: this.divisions.trim(),
      rvfExperience: this.toDiseaseExperienceDto(this.rvfExperience),
      evdExperience: this.toDiseaseExperienceDto(this.evdExperience),
    };

    if (this.hasExistingProfile) {
      this.evaluatorProfileService.updateProfile(dto).subscribe({
        next: () => {
          this.isEditing = false;
          console.log('Evaluator profile updated successfully');
        },
        error: (err) => console.error('Error while updating evaluator profile', err),
      });
    } else {
      this.evaluatorProfileService.saveProfile(dto).subscribe({
        next: () => {
          console.log('Evaluator profile saved successfully');
        },
        error: (err) => console.error('Error while saving evaluator profile', err),
      });
    }
  }

  private loadExistingProfile(): void {
    this.evaluatorProfileService.getProfile().subscribe({
      next: (profile: ResponseEvaluatorProfileDto) => {
        this.selectedProfessions = [...profile.professions];
        this.selectedSectors = [...profile.sectors];
        this.selectedInterventionLevels = [...profile.interventionLevels];
        this.selectedSectorsWorkedIn = [...profile.sectorsWorkedIn];

        this.countries = profile.countries;
        this.regions = profile.regions;
        this.divisions = profile.divisions;

        this.rvfExperience = this.fromDiseaseExperienceDto(profile.rvfExperience);
        this.evdExperience = this.fromDiseaseExperienceDto(profile.evdExperience);

        this.cdr.detectChanges();
      },
      error: (err) => console.error('Error while loading evaluator profile', err),
    });
  }
}
