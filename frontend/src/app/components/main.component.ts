import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-main',
  templateUrl: './main.component.html',
  styleUrl: './main.component.css'
})
export class MainComponent {

  queryParams !: { q: string, loc: string, fullTime: boolean, sortByRecent: boolean };

  constructor(private route: ActivatedRoute) {
    this.route.queryParams.subscribe(params => {
      this.queryParams = {
        q: params['q'],
        loc: params['loc'],
        fullTime: params['fullTime'],
        sortByRecent: params['sortByRecent']
      };
    });
  }
}
