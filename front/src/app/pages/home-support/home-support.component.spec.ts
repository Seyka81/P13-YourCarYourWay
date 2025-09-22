import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeSupportComponent } from './home-support.component';

describe('SupportComponent', () => {
  let component: HomeSupportComponent;
  let fixture: ComponentFixture<HomeSupportComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [HomeSupportComponent],
    }).compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeSupportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
