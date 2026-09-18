import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CadastroPet } from './cadastro-pet';

describe('CadastroPet', () => {
  let component: CadastroPet;
  let fixture: ComponentFixture<CadastroPet>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroPet],
    }).compileComponents();

    fixture = TestBed.createComponent(CadastroPet);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
