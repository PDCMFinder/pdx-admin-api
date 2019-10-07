import {Component, OnInit} from '@angular/core';
import {MappingService} from "./mapping.service";
import {ActivatedRoute, NavigationEnd, Router} from "@angular/router";

@Component({
  selector: 'app-pdxFinder',
  templateUrl: './app.component.html',
  styles: [``]
})
export class AppComponent  implements OnInit {

  private isFullPage: boolean;

  constructor(private router: Router,
              private route: ActivatedRoute) { }

  ngOnInit() {

    // Switch between desired layout when loggedin and when not logged in
    this.router.events.subscribe((e) => {
      if (e instanceof NavigationEnd) {

        if (e.url.includes('login')){
          this.isFullPage = true;
        }
      }
    });




  }


}
