import {Component, Input, OnInit} from '@angular/core';
import {NgForm} from '@angular/forms';
import {AuthenticationService} from '../services/authentication.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {

  mode = 0;
//  @Input() isLogin: boolean;

  constructor(private authenticationService: AuthenticationService,
              private router: Router) { }

  ngOnInit() {
  }

  /*
     Récupère le JWT de l'entête Autorization.
     Enregistre le Token dans le local Storage et decodage du Token
     pour récuperer le username et les roles
  */
  onLogin(dataForm: NgForm) {
    console.log(dataForm);
    this.authenticationService.login(dataForm).subscribe(
      response => {
        // console.log(response.headers.get('Authorization'));
        const jwt = response.headers.get('Authorization');  // On recupère le header authorization contenant le prefixe Bearer et le JWT

        // console.log(jwt);
       this.authenticationService.saveToken(jwt); // On le stock dans le localStorage
        this.router.navigateByUrl('/tasks'); // Redirection vers la route tasks

      }, error1 => {
        this.mode = 1;
        console.log(error1);
      });
  }

}
