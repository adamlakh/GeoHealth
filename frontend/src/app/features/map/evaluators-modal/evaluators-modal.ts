import {Component, Input, Output, EventEmitter, OnChanges, ChangeDetectorRef} from '@angular/core';
import {CommonModule} from '@angular/common';
import {CdkDrag, CdkDragHandle} from '@angular/cdk/drag-drop';
import {UserSummaryDto} from '../../../shared/models/AdminModel/UserModel/UserSummaryDto';
import {AdminEvaluationFormService} from '../../../core/service/AdminService/AdminEvaluationFormService/AdminEvaluationFormService';

@Component({
  selector: 'app-evaluators-modal',
  standalone: true,
  imports: [CommonModule, CdkDrag, CdkDragHandle],
  templateUrl: './evaluators-modal.html',
  styleUrl: './evaluators-modal.css',
})
export class EvaluatorsModalComponent implements OnChanges {
  @Input() mapId: number = -1;
  @Output() close = new EventEmitter<void>();
  @Output() userSelected = new EventEmitter<UserSummaryDto>();

  evaluators: UserSummaryDto[] = [];

  constructor(
    private adminEvaluationFormService: AdminEvaluationFormService,
    private cdr: ChangeDetectorRef) {}

  ngOnChanges(): void {
    if (this.mapId !== -1) {
      this.loadEvaluators();
    }
  }

  private loadEvaluators(): void {
    this.adminEvaluationFormService.getEvaluators(this.mapId).subscribe({
      next: (users) => {
        this.evaluators = users;
        this.cdr.detectChanges();
      },
      error: (err) => {
        console.error('Failed to load evaluators', err);
      }
    });
  }

  onSelectUser(user: UserSummaryDto): void {
    this.userSelected.emit(user);
  }

  onClose(): void {
    this.close.emit();
  }
}
