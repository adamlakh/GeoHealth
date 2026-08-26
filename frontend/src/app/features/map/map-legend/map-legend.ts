import { Component, Input } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'map-legend',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './map-legend.html',
  styleUrl: './map-legend.css',
})
export class MapLegendComponent {
  @Input() mapType: string | undefined = '';
  @Input() season: string | undefined = '';
  @Input() isRaster: boolean = false;

  get isEbola(): boolean {
    return this.mapType === 'EBOLA';
  }

  get isRiftValley(): boolean {
    return this.mapType === 'RIFT_VALLEY_FEVER';
  }

  get isDry(): boolean {
    return this.season === 'DRY' || this.season === 'ALL_SEASON';
  }
}
