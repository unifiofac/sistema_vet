import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PainelVeterinario } from './painel-veterinario';

describe('PainelVeterinario', () => {
  let component: PainelVeterinario;
  let fixture: ComponentFixture<PainelVeterinario>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PainelVeterinario],
    }).compileComponents();

    fixture = TestBed.createComponent(PainelVeterinario);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
