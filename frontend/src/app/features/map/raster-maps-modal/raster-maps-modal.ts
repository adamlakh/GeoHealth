import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDrag, CdkDragHandle } from '@angular/cdk/drag-drop';
import { RasterMapListDto } from '../../../shared/models/MapModel/RasterMapModel/RasterMapListDto';
import { ButtonComponent } from '../../../shared/components/button.component/button.component';

@Component({
  selector: 'app-raster-maps-modal',
  standalone: true,
  imports: [CommonModule, CdkDrag, CdkDragHandle, ButtonComponent],
  templateUrl: './raster-maps-modal.html',
  styleUrl: './raster-maps-modal.css',
})
export class RasterMapsModalComponent {
  @Input() rasterMaps: RasterMapListDto[] = [];
  @Input() mapId: number = -1;

  @Output() close = new EventEmitter<void>();
  @Output() deleteRaster = new EventEmitter<number>();
  @Output() uploadRequested = new EventEmitter<void>();

  onDelete(id: number): void {
    this.deleteRaster.emit(id);
  }

  onUploadClick(): void {
    this.uploadRequested.emit();
  }

  onClose(): void {
    this.close.emit();
  }
}
