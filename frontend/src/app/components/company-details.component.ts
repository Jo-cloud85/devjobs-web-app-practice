import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import { Company } from '../models/company.model';
import * as L from 'leaflet';
import { CompanyService } from '../services/company.service';
import { ActivatedRoute } from '@angular/router';
import { Subscription } from 'rxjs';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'app-company-details',
  templateUrl: './company-details.component.html',
  styleUrl: './company-details.component.css'
})
export class CompanyDetailsComponent implements OnInit, OnDestroy {

  company !: Company;
  private readonly companySvc = inject(CompanyService);
  private readonly activatedRoute = inject(ActivatedRoute);

  private sub$ !: Subscription;

  ngOnInit(): void {
    const companyName = this.activatedRoute.snapshot.params["company"];
    this.sub$ = this.companySvc.getCompanyByName(companyName)
      .subscribe({
        next: (result : any) => {
          this.company = result; 
          //console.log(">>>> Result: " + JSON.stringify(result)); 
        },
        error: (error: HttpErrorResponse) => {console.log(">>>> ERROR: " + error)},
        complete: () => {console.log("Find company successful!")}
      })
  }

  ngAfterViewInit(): void {
    setTimeout(() => {
      this.initMap();
    });
  }
  
  initMap(): void {
    const map = L.map('map').setView([51.505, -0.09], 13);
    const marker = L.marker([51.5, -0.09]).addTo(map);
    marker.bindPopup("We are here!").openPopup();
    // const map = L.map('map').setView(this.company.address.coord, 13);

    L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 19,
    }).addTo(map);

    L.marker(this.company.coordinates).addTo(map)
      .bindPopup(`<b>${this.company.company}</b><br>${this.company.address}`)
      .openPopup();
  }

  ngOnDestroy(): void {
    this.sub$.unsubscribe();
  }
}
