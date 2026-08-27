import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RasterMapsModal } from './raster-maps-modal';

describe('RasterMapsModal', () => {
  let component: RasterMapsModal;
  let fixture: ComponentFixture<RasterMapsModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RasterMapsModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RasterMapsModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
