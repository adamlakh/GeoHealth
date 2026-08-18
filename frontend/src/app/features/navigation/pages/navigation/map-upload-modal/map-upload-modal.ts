import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { CommonModule } from '@angular/common';
import {FinalMapFormComponent} from './final-map-form/final-map-form'

@Component({
  selector: 'app-map-upload-modal',
  standalone: true,
  imports: [CommonModule, FinalMapFormComponent],
  templateUrl: './map-upload-modal.html',
  styleUrl: './map-upload-modal.css'
})
export class MapUploadModalComponent {
  constructor(@Inject(MAT_DIALOG_DATA) public data: {typeOfPopUp: string }) {}

}
