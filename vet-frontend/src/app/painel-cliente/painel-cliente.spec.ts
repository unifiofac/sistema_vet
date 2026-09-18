import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PainelCliente } from './painel-cliente';

describe('PainelCliente', () => {
  let component: PainelCliente;
  let fixture: ComponentFixture<PainelCliente>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PainelCliente],
    }).compileComponents();

    fixture = TestBed.createComponent(PainelCliente);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
