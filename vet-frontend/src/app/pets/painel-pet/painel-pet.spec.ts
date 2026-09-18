import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PainelPet } from './painel-pet';

describe('PainelPet', () => {
  let component: PainelPet;
  let fixture: ComponentFixture<PainelPet>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PainelPet],
    }).compileComponents();

    fixture = TestBed.createComponent(PainelPet);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
