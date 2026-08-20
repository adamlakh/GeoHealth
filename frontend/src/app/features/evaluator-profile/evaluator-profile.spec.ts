import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EvaluatorProfile } from './evaluator-profile';

describe('EvaluatorProfile', () => {
  let component: EvaluatorProfile;
  let fixture: ComponentFixture<EvaluatorProfile>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [EvaluatorProfile]
    })
    .compileComponents();

    fixture = TestBed.createComponent(EvaluatorProfile);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
