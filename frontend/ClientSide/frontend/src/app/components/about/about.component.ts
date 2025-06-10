import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';

@Component({
  selector: 'app-about',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './about.component.html',
  styleUrls: ['./about.component.css'],
})
export class AboutComponent {
  teamMembers = [
    {
      name: 'Marco Rossi',
      role: 'Head Chef',
      image: '/placeholder.svg?height=300&width=300',
      description:
        'With 20 years of experience in Italian cuisine, Marco brings authentic flavors from his hometown in Tuscany.',
    },
    {
      name: 'Sofia Bianchi',
      role: 'Sous Chef',
      image: '/placeholder.svg?height=300&width=300',
      description:
        'Sofia specializes in traditional pasta making and has trained in the finest kitchens of Rome.',
    },
    {
      name: 'Giuseppe Verdi',
      role: 'Pastry Chef',
      image: '/placeholder.svg?height=300&width=300',
      description:
        'Giuseppe creates our exquisite desserts, bringing the sweet traditions of Sicily to every plate.',
    },
  ];
}
