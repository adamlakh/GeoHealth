import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CdkDrag, CdkDragHandle } from '@angular/cdk/drag-drop';
import { FormsModule } from '@angular/forms';
import { AdminRasterMapService } from '../../../core/service/AdminService/AdminMapService/AdminRasterMapService';

@Component({
  selector: 'app-raster-upload-modal',
  standalone: true,
  imports: [CommonModule, FormsModule, CdkDrag, CdkDragHandle],
  templateUrl: './raster-upload-modal.html',
  styleUrl: './raster-upload-modal.css',
})
export class RasterUploadModalComponent {
  @Input() mapId: number = -1;
  @Output() close = new EventEmitter<boolean>();

  title: string = '';
  description: string = '';
  selectedTifFile: File | null = null;
  isUploading = false;
  errorMessage = '';

  constructor(private adminRasterMapService: AdminRasterMapService) {}

  onTifFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedTifFile = input.files[0];
    }
  }

  isFormValid(): boolean {
    return !!this.title.trim() && !!this.description.trim() && !!this.selectedTifFile;
  }

  onSubmit(): void {
    if (!this.isFormValid() || this.mapId === -1) {
      this.errorMessage = 'Please fill in all fields and select a TIF file.';
      return;
    }

    const formData = new FormData();
    formData.append('title', this.title);
    formData.append('description', this.description);
    formData.append('tifFile', this.selectedTifFile as File);

    this.isUploading = true;
    this.errorMessage = '';

    console.log('[raster-upload] starting upload for mapId=', this.mapId, 'file=', this.selectedTifFile?.name);

    this.adminRasterMapService.uplaodNewRasterMap(this.mapId, formData).subscribe({
      next: (result) => {
        console.log('[raster-upload] upload succeeded', result);
        this.isUploading = false;
        this.close.emit(true);
      },
      error: (err) => {
        console.error('[raster-upload] upload failed', err);
        this.isUploading = false;
        this.errorMessage = 'Upload failed. Please try again.';
      }
    });

  setTimeout(() => {
    this.isUploading = false;
    this.close.emit(true);
  }, 5000);
  }

  onClose(): void {
    this.close.emit(false);
  }
}
