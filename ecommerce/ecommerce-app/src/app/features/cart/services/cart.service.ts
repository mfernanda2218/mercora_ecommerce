import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Cart } from '../../../core/models/cart.models';

@Injectable({
  providedIn: 'root'
})
export class CartService {
  private apiUrl = '/api/cart';

  constructor(private http: HttpClient) { }

  getCart(): Observable<Cart> {
    return this.http.get<Cart>(this.apiUrl);
  }

  addItem(productId: string, quantity: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/items`, { productId, quantity });
  }

  updateItemQuantity(itemId: string, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/items/${itemId}?quantity=${quantity}`, {});
  }

  removeItem(itemId: string): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/items/${itemId}`);
  }

  clearCart(): Observable<void> {
    return this.http.delete<void>(this.apiUrl);
  }
}
