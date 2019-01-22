import {Component, Input, OnInit} from '@angular/core';
import {AuthenticationService} from '../services/authentication.service';
import {Task} from '../models/task.model';
import {Router} from '@angular/router';

@Component({
  selector: 'app-tasks',
  templateUrl: './tasks.component.html',
  styleUrls: ['./tasks.component.css']
})
export class TasksComponent implements OnInit {

  tasks: Task[] = null;
  // @Input() isLogin: boolean = false;
  authService: AuthenticationService = this.authenticationService;

  constructor(private authenticationService: AuthenticationService,
              private router: Router) { }

  // Une fois un composant est chagé il appelle ngOnInit
  ngOnInit() {
    this.authenticationService.getTasks().subscribe(
      (data: Task[]) => {
        this.tasks = data;
        console.log(data);
       // this.isLogin = true;
    }, error1 => {
        /*
            Dans le cas où l'on arrive pas à récuperer les taches;
            Dans ce cas, soit le Token a expiré ou soit modifié dans le local storage.
            Donc Dans logout on va détruire d'abord le Token
            Après on va vers login
         */
        this.authenticationService.logout();
        this.router.navigateByUrl('/login');
      });
  }

  onNewTask() {
    this.router.navigateByUrl('/new-task');
  }




}
