import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluatorsModal } from './evaluators-modal';

describe('EvaluatorsModal', () => {
  let component: EvaluatorsModal;
  let fixture: ComponentFixture<EvaluatorsModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvaluatorsModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EvaluatorsModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
