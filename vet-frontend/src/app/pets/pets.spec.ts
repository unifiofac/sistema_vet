import { TestBed } from '@angular/core/testing';
import { Pets } from './pets';

describe('Pets', () => {
  let service: Pets;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Pets);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
