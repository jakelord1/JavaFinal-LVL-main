export interface Ingredient {
  id: number;
  name: string;
  description: string;
  category: string;
}

export interface RecipePosition {
  id: number;
  ingredient_id: number;
  recipe_id: number;
  amount: number;
  unit: string;
  notes: string;
}

export interface Recipe {
  id: number;
  dish_name: string;
  cook_time: number;
  dish_shorttext: string;
  recipe_fulltext: string;
  categories: string;
  image?: string;
  recipe_positions?: RecipePosition[];
}

export const ingredients: Ingredient[] = [
  { id: 1, name: 'Картофель', description: 'Свежий картофель', category: 'Овощи' },
  { id: 2, name: 'Куриное филе', description: 'Охлажденное', category: 'Мясо' },
  { id: 3, name: 'Лук', description: 'Репчатый', category: 'Овощи' },
  { id: 4, name: 'Морковь', description: 'Свежая', category: 'Овощи' },
  { id: 5, name: 'Соль', description: 'Поваренная', category: 'Специи' },
  { id: 6, name: 'Перец', description: 'Черный молотый', category: 'Специи' },
];

export const recipes: Recipe[] = [
  {
    id: 1,
    dish_name: 'Жареная картошка с курицей',
    cook_time: 45,
    dish_shorttext: 'Простое и сытное блюдо на каждый день.',
    recipe_fulltext: '1. Нарежьте картофель, курицу, лук и морковь. 2. Обжарьте курицу до золотистой корочки. 3. Добавьте овощи и жарьте до готовности. 4. Посолите, поперчите по вкусу.',
    categories: 'Второе блюдо',
    image: 'https://via.placeholder.com/150',
  },
];

export const recipePositions: RecipePosition[] = [
  { id: 1, recipe_id: 1, ingredient_id: 1, amount: 500, unit: 'гр', notes: 'Нарезать кубиками' },
  { id: 2, recipe_id: 1, ingredient_id: 2, amount: 300, unit: 'гр', notes: 'Нарезать кусочками' },
  { id: 3, recipe_id: 1, ingredient_id: 3, amount: 1, unit: 'шт', notes: 'Нарезать полукольцами' },
  { id: 4, recipe_id: 1, ingredient_id: 4, amount: 1, unit: 'шт', notes: 'Натереть на терке' },
  { id: 5, recipe_id: 1, ingredient_id: 5, amount: 1, unit: 'ч.л.', notes: 'По вкусу' },
  { id: 6, recipe_id: 1, ingredient_id: 6, amount: 0.5, unit: 'ч.л.', notes: 'По вкусу' },
];