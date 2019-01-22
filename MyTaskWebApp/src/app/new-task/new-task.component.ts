import { Component, OnInit } from '@angular/core';
import {AuthenticationService} from '../services/authentication.service';
import {Task} from '../models/task.model';

@Component({
  selector: 'app-new-task',
  templateUrl: './new-task.component.html',
  styleUrls: ['./new-task.component.css']
})
export class NewTaskComponent implements OnInit {

  task: Task = new Task();
  mode: number= 1;

  constructor(private autheticationService: AuthenticationService) { }

  ngOnInit() {
  }

  onSaveTask(task) {
    this.autheticationService.saveTask(task).subscribe(
      (response: Task) => {
          this.task = response;
          console.log(response);
          this.mode = 2;
      }, error1 => {
        this.mode = 0;
        console.log(error1);
      });
  }

}
