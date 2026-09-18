import { ComponentFixture, TestBed } from '@angular/core/testing';
import { CadastroPaciente } from './cadastro-paciente';

describe('CadastroPaciente', () => {
  let component: CadastroPaciente;
  let fixture: ComponentFixture<CadastroPaciente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CadastroPaciente],
    }).compileComponents();

    fixture = TestBed.createComponent(CadastroPaciente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
