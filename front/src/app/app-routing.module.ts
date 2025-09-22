import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HomeComponent } from './pages/home/home.component';
import { RegisterComponent } from './pages/register/register.component';
import { LoginComponent } from './pages/login/login.component';
import { AuthGuard } from './guards/auth.guard';
import { UnauthGuard } from './guards/unauth.guard';
import { UserComponent } from './pages/user/user.component';
import { SupportComponent } from './pages/support/support.component';
import { HomeSupportComponent } from './pages/home-support/home-support.component';

// consider a guard combined with canLoad / canActivate route option
// to manage unauthenticated user to access private routes
const routes: Routes = [
  {
    path: '',
    redirectTo: 'home',
    pathMatch: 'full',
  },
  { path: 'home', canActivate: [UnauthGuard], component: HomeComponent },
  {
    path: 'register',
    canActivate: [UnauthGuard],
    component: RegisterComponent,
  },
  {
    path: 'homesupport',
    canActivate: [AuthGuard],
    component: HomeSupportComponent,
  },
  {
    path: 'support',
    component: SupportComponent,
  },
  { path: 'login', canActivate: [UnauthGuard], component: LoginComponent },
  { path: 'user', canActivate: [AuthGuard], component: UserComponent },
  { path: '**', redirectTo: '' },
];
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
