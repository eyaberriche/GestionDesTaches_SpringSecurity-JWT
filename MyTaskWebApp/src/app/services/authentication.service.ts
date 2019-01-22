import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {JwtHelperService} from '@auth0/angular-jwt';
import {Task} from '../models/task.model';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {

  private host: string = 'http://localhost:8080';
  private jwtToken = null;  // C'est le Token qui est recupéré du LocalStorage
  private roles: Array<any>;

  constructor(private http: HttpClient) { }

  /*
	Permet l'authentification
	{observe: 'response'} : Permet de récupérer le contenu de la réponse, cela met permet
	de récupérer l'entête Authorization
	{observe: 'response'} :  Ce n'est plus la peine de me convertir le resultat en format
	JSON; donne moi toute la reponse http.
	Ensuite dans la reponse http, je peux récuperer les entêtes, le contenu
  */
  login(user) {
    return this.http.post(this.host + '/login', user, {observe: 'response'});
  }

  /*
      Le Token, on va le mettre dans le LocalStorage.
      Dans le localStorage j'ajoute un attribut setItem qui s'appele token
      dont la valeur est jwt.
      Dans saveToken, on va définir un JWTHelper, ensuite on va s'en service pour decoder le Token.
      Ce qui va nous interesser c'est les claims roles
   */
  saveToken(jwt: string) {
    this.jwtToken = jwt;
    localStorage.setItem('token', jwt);  // On stock le Token dans le Locale Storage
    const jwtHelper = new JwtHelperService(); // On décode le Token
    this.roles = jwtHelper.decodeToken(this.jwtToken).roles; // dans jwtToken j'ai un attribut qui s'appelle roles
  }

  // On recupère le token dans le local storage
  loadToken() {
    this.jwtToken = localStorage.getItem('token');
  }

  /*
       Récupération de la liste des tâches.
       Si je lui envoie la requête qu' avec this.host + '/tasks'
       il ne va pas me l'accepter. Il faut que je lui envoie le Token
       Comment envoyer le Token ?
       Je dois envoyer dans les Options un objet qui contient headers avec new HttpHeaders,
       dans lequel il y a un objet Authorization, et dans lequel il y a this.jwtToken (le jwt)
       Déclaration de la variable jwtToken et la methode loadToken() afin
       de charger le token et de le passer à spring security
	   {headers: new HttpHeaders({'Authorization' : this.jwtToken})} : A chaque fois j'envoie une
	   requête (get, post, put, delete), j'envoie l'entête Authorization
   */
  getTasks() {

    if(this.jwtToken == null)
      this.loadToken();

    return this.http.get(this.host + '/tasks',
      {headers: new HttpHeaders({'Authorization' : this.jwtToken})});
  }

  saveTask(task: Task) {

    if(this.jwtToken == null)
      this.loadToken();

    return this.http.post(this.host + '/tasks', task,
      {headers: new HttpHeaders({'Authorization' : this.jwtToken})});
  }


  // On supprime le Token dans le local Storage
  // Il faut aussi renitialier jwtToken
  logout() {
    this.jwtToken = null;
    localStorage.removeItem('token');
  }

  /*
    Que les utilisateurs ayant le rôle  ADMIN ont le droit d'acceder au  formulaire afin
    d'ajouter une nouvelle tâche
    Ici on va utiliser la dépendance qui angular2-jwt
    On va ajouter dans le contexte de l'application les roles de l'utilisateur, ce sera un tableau.
    private roles: Array<any>=[]. On va définir les rôles dans la méthodes saveToken
    Dans saveToken, on va définir un JWTHelperService, enuite on va s'en service pour decoder le Token.
    Ce qui va nous interreser c'est les claims roles.(cf methode saveToken)
    Les rôles sont sous forme clé: valeur; la clé se nomme authority. Pour récupérer les rôles
    on se sert de la clé
   */
  isAdmin() {
    for (const role of this.roles) {
      if(role.authority === 'ADMIN')
        return true;
    }
    return false;
  }


}


