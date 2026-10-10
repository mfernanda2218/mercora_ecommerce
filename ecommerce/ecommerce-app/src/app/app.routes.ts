import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'products',
    loadComponent: () => import('./features/products/pages/product-list/product-list').then(m => m.ProductList)
  },
  {
    path: 'products/:id',
    loadComponent: () => import('./features/products/pages/product-detail/product-detail').then(m => m.ProductDetail)
  },
  {
    path: 'cart',
    loadComponent: () => import('./features/cart/pages/cart/cart').then(m => m.CartComponent)
  },
  {
    path: 'admin/products',
    loadComponent: () => import('./features/admin/pages/admin-product-list/admin-product-list').then(m => m.AdminProductList)
  },
  {
    path: 'admin/products/new',
    loadComponent: () => import('./features/admin/pages/admin-product-form/admin-product-form').then(m => m.AdminProductForm)
  },
  {
    path: '',
    redirectTo: '/products',
    pathMatch: 'full'
  }
];
