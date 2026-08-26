import {Component, EventEmitter, Input, Output} from '@angular/core';
import {TranslocoPipe} from '@jsverse/transloco';

type Option = string | { value: string; label: string };

@Component({
  selector: 'app-rating-scaler',
  standalone: true,
  templateUrl: './rating-scaler.html',
  styleUrl: './rating-scaler.css',
  imports: [
    TranslocoPipe
  ]
})
export class RatingScalerComponent {

  @Input() name: string = '';
  @Input() options: Option[] = [];
  @Input() value: string = '';
  @Input() hasTraduction: boolean= false
  @Output() valueChange = new EventEmitter<string>();
  @Input() fillStyle: boolean = true;

  onSelect(option: Option): void {
    const value = this.getValue(option);

    if (this.value === value) {
      this.value = '';
      this.valueChange.emit('');
    } else {
      this.value = value;
      this.valueChange.emit(value);
    }
  }

  getValue(option: Option): string {
    return typeof option === 'string' ? option : option.value;
  }

  getLabel(option: Option): string {
    return typeof option === 'string' ? option : option.label;
  }
}
