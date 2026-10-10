export interface Category {
  id: number;
  name: string;
  description: string;
}

export interface ProductImage {
  id: string;
  url: string;
  isMain: boolean;
}

export interface Product {
  id: string;
  category: Category;
  name: string;
  description: string;
  price: number;
  stock: number;
  active: boolean;
  images: ProductImage[];
}
