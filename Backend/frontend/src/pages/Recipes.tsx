import { useState, useEffect } from 'react';
import { Button, Accordion } from 'react-bootstrap';
import { NavLink } from 'react-router-dom';
import { recipes as mockRecipes, type Recipe } from '../data/mockData';
import './Recipes.css';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost';

const Recipes = () => {
  const [recipes, setRecipes] = useState<Recipe[]>(mockRecipes);

  const page = 0;

  useEffect(() => {
      fetch(`${API_URL}:8080/homecook/recipes?page=${page}`, {
        method: 'GET',
      })
        .then((res) => {
          if (!res.ok) throw new Error('Failed to fetch recipes');
          return res.json();
        })
        .then((data: Recipe[]) => setRecipes(data))
        .catch((err) => console.error(err));
    }, []);

  const handleDelete = (e: React.MouseEvent, id: number) => {
    e.stopPropagation();
    if (window.confirm('Are you sure you want to delete this Recipe?')) {
        fetch(`${API_URL}:8080/homecook/recipes?id=${id}`, {
          method: 'DELETE',
        })
          .then((res) => {
            if (!res.ok) throw new Error('Failed to delete Recipe');
            setRecipes((prev) => prev.filter((ing) => ing.id !== id));
          })
          .catch((err) => console.error(err));
      };
  };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Управление Рецептами</h2>
        <NavLink to="/homecook/recipe/new" className="btn btn-primary">
          <i className="bi bi-plus-lg me-2"></i>
          Создать Рецепт
        </NavLink>
      </div>

      <Accordion defaultActiveKey="0" alwaysOpen className="recipe-accordion">
        {recipes.map((recipe) => (
          <Accordion.Item eventKey={String(recipe.id)} key={recipe.id}>
            <Accordion.Header>
              <div className="recipe-accordion-header-content">
                <span className="recipe-title">{recipe.dish_name}</span>
                <div className="recipe-actions">
                  <NavLink
                    to={`/homecook/recipe/${recipe.id}`}
                    className="btn btn-outline-primary"
                    onClick={(e) => e.stopPropagation()}
                    title="Редактировать"
                  >
                    <i className="bi bi-pencil"></i>
                  </NavLink>
                  <Button
                    variant="outline-danger"
                    className="btn-icon"
                    onClick={(e) => handleDelete(e, recipe.id)}
                    title="Удалить"
                  >
                    <i className="bi bi-trash"></i>
                  </Button>
                </div>
              </div>
            </Accordion.Header>
            <Accordion.Body>
              <div className="recipe-detail">
                <strong>Картинка:</strong> <a href={recipe.image}>{recipe.image}</a>
              </div>
              <div className="recipe-detail">
                <strong>Краткое описание:</strong> {recipe.dish_shorttext}
              </div>
              <div className="recipe-detail">
                <strong>Время готовки:</strong> {recipe.cook_time} минут
              </div>
              <div className="recipe-detail">
                <strong>Категория:</strong> 
                <span className="badge bg-primary">{recipe.categories}</span>
              </div>
              <div className="recipe-detail">
                <h6 className="mb-3">Полный рецепт:</h6>
                <div className="recipe-fulltext">{recipe.recipe_fulltext}</div>
              </div>
            </Accordion.Body>
          </Accordion.Item>
        ))}
      </Accordion>
    </div>
  );
};

export default Recipes;
