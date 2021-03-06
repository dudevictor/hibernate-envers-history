import { Component, OnInit } from '@angular/core';
import {LoadingService} from "../../services/http/loading.service";

@Component({
  selector: 'app-page',
  templateUrl: './page.component.html',
  styleUrls: ['./page.component.css']
})
export class PageComponent implements OnInit {

  constructor(public loading : LoadingService) { }

  ngOnInit() {
  }

}
