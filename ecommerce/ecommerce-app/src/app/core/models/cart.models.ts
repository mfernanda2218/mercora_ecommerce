import { Product } from './catalog.models';

export interface CartItem {
  id: string;
  product: Product;
  quantity: number;
}

export interface Cart {
  id: string;
  items: CartItem[];
  total?: number; // Calculated field
}
