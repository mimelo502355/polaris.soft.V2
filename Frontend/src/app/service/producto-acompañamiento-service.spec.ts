import { TestBed } from '@angular/core/testing';

import { ProductoAcompañamientoService } from './producto-acompañamiento-service';

describe('ProductoAcompañamientoService', () => {
  let service: ProductoAcompañamientoService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ProductoAcompañamientoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
