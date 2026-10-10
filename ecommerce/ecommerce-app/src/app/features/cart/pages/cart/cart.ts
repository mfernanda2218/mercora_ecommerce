import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';

import { CartService } from '../../services/cart.service';
import { Cart } from '../../../core/models/cart.models';

@Component({
  selector: 'app-cart',
  standalone: true,
  imports: [CommonModule, RouterModule, MatButtonModule, MatIconModule, MatCardModule],
  templateUrl: './cart.html',
  styleUrls: ['./cart.scss']
})
export class CartComponent implements OnInit {
  cart: Cart | null = null;
  loading = true;

  constructor(private cartService: CartService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    this.cartService.getCart().subscribe({
      next: (res) => {
        this.cart = this.calculateTotal(res);
        this.loading = false;
      },
      error: () => {
        this.loading = false;
      }
    });
  }

  calculateTotal(cart: Cart): Cart {
    const total = cart.items.reduce((sum, item) => sum + (item.product.price * item.quantity), 0);
    return { ...cart, total };
  }

  updateQuantity(itemId: string, newQuantity: number): void {
    if (newQuantity <= 0) return;
    this.cartService.updateItemQuantity(itemId, newQuantity).subscribe(res => {
      this.cart = this.calculateTotal(res);
    });
  }

  removeItem(itemId: string): void {
    this.cartService.removeItem(itemId).subscribe(res => {
      this.cart = this.calculateTotal(res);
    });
  }

  clearCart(): void {
    this.cartService.clearCart().subscribe(() => {
      if (this.cart) {
        this.cart = { ...this.cart, items: [], total: 0 };
      }
    });
  }
}
