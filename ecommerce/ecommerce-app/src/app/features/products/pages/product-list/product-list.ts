import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule } from '@angular/forms';

import { ProductService } from '../../services/product.service';
import { CategoryService } from '../../services/category.service';
import { Product, Category } from '../../../core/models/catalog.models';

@Component({
  selector: 'app-product-list',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    MatCardModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule
  ],
  templateUrl: './product-list.html',
  styleUrls: ['./product-list.scss']
})
export class ProductList implements OnInit {
  products: Product[] = [];
  filteredProducts: Product[] = [];
  categories: Category[] = [];

  searchTerm = '';
  selectedCategoryId: number | '' = '';
  sortOrder: 'asc' | 'desc' = 'asc';

  constructor(
    private productService: ProductService,
    private categoryService: CategoryService
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  loadData(): void {
    this.categoryService.getCategories().subscribe(res => this.categories = res);
    this.productService.getProducts().subscribe(res => {
      this.products = res;
      this.applyFilters();
    });
  }

  applyFilters(): void {
    let result = this.products;

    if (this.searchTerm) {
      result = result.filter(p => p.name.toLowerCase().includes(this.searchTerm.toLowerCase()));
    }

    if (this.selectedCategoryId !== '') {
      result = result.filter(p => p.category.id === this.selectedCategoryId);
    }

    result.sort((a, b) => {
      if (this.sortOrder === 'asc') return a.price - b.price;
      return b.price - a.price;
    });

    this.filteredProducts = result;
  }
}
