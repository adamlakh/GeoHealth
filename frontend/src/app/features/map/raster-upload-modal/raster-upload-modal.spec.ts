import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RasterUploadModal } from './raster-upload-modal';

describe('RasterUploadModal', () => {
  let component: RasterUploadModal;
  let fixture: ComponentFixture<RasterUploadModal>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RasterUploadModal]
    })
    .compileComponents();

    fixture = TestBed.createComponent(RasterUploadModal);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
