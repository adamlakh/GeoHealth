import { Component, OnInit } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { AdminFinalMapService } from '../../../../../../core/service/AdminService/AdminMapService/AdminFinalMapService';
import { InputboxComponents } from '../../../../../../shared/components/inputbox.components/inputbox.components';
import { MatDialogRef } from '@angular/material/dialog';
import {disabled} from '@angular/forms/signals';

@Component({
  selector: 'app-final-map-form',
  standalone: true,
  imports: [ReactiveFormsModule, InputboxComponents],
  templateUrl: './final-map-form.html',
  styleUrl: './final-map-form.css'
})
export class FinalMapFormComponent implements OnInit {

  constructor(
    private dialog: MatDialogRef<FinalMapFormComponent>,
    private adminFinalMapService: AdminFinalMapService
  ) {}

  formGroup!: FormGroup;
  selectedZipFile: File | null = null;
  selectedTifFiles: File[] = [];
  selectedSeason?: string;
  selectedDisease?: string;

  isUploading = false;

  ngOnInit(): void {
    this.formGroup = new FormGroup({
      title: new FormControl('', [Validators.required, Validators.minLength(1)]),
      description: new FormControl('', [Validators.required, Validators.minLength(1)]),
      zipFile: new FormControl(null, [Validators.required]),
      tifFiles: new FormControl(null, [Validators.required]),
      tagSeason : new FormControl('', [Validators.required]),
      tagDisease : new FormControl('', [Validators.required])
    });

  }


  onZipFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      this.selectedZipFile = input.files[0];
      this.formGroup.get('zipFile')?.setValue(this.selectedZipFile.name);
    }
  }

  onTifFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;
    if (input.files && input.files.length > 0) {
      const newFiles = Array.from(input.files);
      this.selectedTifFiles = [...this.selectedTifFiles, ...newFiles];
      this.formGroup.get('tifFiles')?.setValue(this.selectedTifFiles.length ? this.selectedTifFiles : null);
      this.formGroup.get('tifFiles')?.markAsDirty();
    }
    input.value = '';
  }


  removeTifFile(index: number): void {
    this.selectedTifFiles = this.selectedTifFiles.filter((_, i) => i !== index);
    this.formGroup.get('tifFiles')?.setValue(this.selectedTifFiles.length ? this.selectedTifFiles : null);
    this.formGroup.get('tifFiles')?.markAsDirty();
  }

  isFieldValid(name: string): boolean {
    const formControl = this.formGroup.get(name);
    return !!(formControl?.invalid && formControl?.dirty);
  }

  onSubmit(): void {
    if (!this.selectedZipFile || this.selectedTifFiles.length === 0) {
      return;
      }

    const tags = [
      this.formGroup.value.tagSeason,
      this.formGroup.value.tagDisease
    ];

    const formData = new FormData();
    formData.append('title', this.formGroup.value.title);
    formData.append('description', this.formGroup.value.description);
    tags.forEach(tag => formData.append('tags', tag));
    formData.append('zipFile', this.selectedZipFile);
    this.selectedTifFiles.forEach(tifFile => formData.append('tifFiles', tifFile));

    this.adminFinalMapService.uploadNewMap(formData).subscribe({
      next: () => {},
      error: (err) => {
        console.error(err);
      }
    });

    this.dialog.close({ uploading: true });

  }

  protected readonly disabled = disabled;
}
