import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [RouterLink, CommonModule],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css'],
})
export class HomeComponent {
  testimonials = [
    {
      name: 'Sarah Johnson',
      text: 'Amazing food and wonderful atmosphere. The pasta was absolutely divine!',
      rating: 5,
    },
    {
      name: 'Michael Chen',
      text: 'Best Italian restaurant in the city. The service was impeccable.',
      rating: 5,
    },
    {
      name: 'Emma Davis',
      text: 'Perfect for a romantic dinner. The tiramisu is to die for!',
      rating: 5,
    },
  ];

  featuredDish = {
    name: 'Truffle Risotto',
    description:
      'Creamy arborio rice with black truffle, parmesan, and fresh herbs',
    price: 28,
    image: '/placeholder.svg?height=300&width=400',
  };
}
