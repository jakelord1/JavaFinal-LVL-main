import { useState, useEffect } from 'react';
import { Button, Form, Container, Row, Col } from 'react-bootstrap';
import { useParams, useNavigate } from 'react-router-dom';
import {
  type Recipe,
  type Ingredient,
  type RecipePosition,
} from '../data/mockData';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost';

const RecipeForm = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const isEditing = id !== undefined;

  const [formData, setFormData] = useState<Omit<Recipe, 'id'>>({
    dish_name: '',
    cook_time: 0,
    dish_shorttext: '',
    recipe_fulltext: '',
    categories: '',
  });
  const [currentRecipePositions, setCurrentRecipePositions] = useState<RecipePosition[]>([]);
  const [availableIngredients, setAvailableIngredients] = useState<Ingredient[]>([]);


  useEffect(() => {
  fetch(`${API_URL}:8080/homecook/ingredients`, { method: 'GET', })
    .then(res => res.json())
    .then(data => setAvailableIngredients(data))
    .catch(err => console.error(err));

  if (isEditing) {
    fetch(`${API_URL}:8080/homecook/recipes?type=id&id=${id}`, { method: 'GET', })
      .then(res => res.json())
      .then(recipe => {
        setFormData(recipe);
        setCurrentRecipePositions(recipe.recipe_positions ?? []);
        console.log(recipe.recipe_positions);
      })
      .catch(err => console.error(err));
  } else {
    setFormData({
      dish_name: '',
      cook_time: 0,
      dish_shorttext: '',
      recipe_fulltext: '',
      categories: '',
    });
    setCurrentRecipePositions([]);
  }  
}, [id, isEditing]);

  const handleFormChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };

  const handleRecipePositionChange = (index: number, e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    const updatedPositions = [...currentRecipePositions];
    updatedPositions[index] = { ...updatedPositions[index], [name]: name === 'amount' || name === 'ingredient_id' ? parseInt(value) : value };
    setCurrentRecipePositions(updatedPositions);
  };

  const handleAddRecipePosition = () => {
    setCurrentRecipePositions([...currentRecipePositions, { id: 0, ingredient_id: 0, recipe_id: isEditing ? parseInt(id!) : 0, amount: 0, unit: '', notes: '' }]);
  };

  const handleRemoveRecipePosition = (posId: number) => {
    setCurrentRecipePositions(currentRecipePositions.filter((rp) => rp.id !== posId));
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    console.log('Form Data:', formData);
        if (isEditing) {
          const updatedRecipe = {
          ...formData,
          id: parseInt(id!)};
          updatedRecipe.recipe_positions = currentRecipePositions.map(pos => {
            const newPos = { ...pos }; 
            delete newPos['ingredient' as keyof typeof newPos];
            return newPos;
          });
          console.log("Sending to backend:", JSON.stringify(updatedRecipe, null, 2));
        // update
        fetch(`${API_URL}:8080/homecook/recipes?id=${id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(updatedRecipe),
        })
          .then((res) => {
            if (!res.ok) throw new Error('Failed to update recipe');
            return res.json();
          })
          .catch((err) => console.error(err));
    } else {
      const newRecipe = { ...formData, id: null };
      // create
      newRecipe.recipe_positions = currentRecipePositions;
      newRecipe.recipe_positions.forEach(recipePositions => {
        recipePositions.id = 0;
      });
      console.log('Creating new recipe:', newRecipe);
      console.log('Creating new recipe positions:', currentRecipePositions);
        fetch(`${API_URL}:8080/homecook/recipes?action=create`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(newRecipe),
        })
          .then((res) => {
            if (!res.ok) throw new Error('Failed to create recipe');
            return res.json();
          })
          .catch((err) => console.error(err));
    }
    navigate('/homecook/recipes');
  };

  return (
    <Container>
      <h2>{isEditing ? 'Редактировать Рецепт' : 'Создать Рецепт'}</h2>
      <Form onSubmit={handleSubmit}>
        <Form.Group className="mb-3">
          <Form.Label>Название Блюда</Form.Label>
          <Form.Control
            type="text"
            name="dish_name"
            value={formData.dish_name}
            onChange={handleFormChange}
            required
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Картинка</Form.Label>
          <Form.Control
            type="text"
            name="image"
            value={formData.image}
            onChange={handleFormChange}
            required
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Время Готовки (мин)</Form.Label>
          <Form.Control
            type="number"
            name="cook_time"
            value={formData.cook_time}
            onChange={handleFormChange}
            required
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Краткое Описание</Form.Label>
          <Form.Control
            as="textarea"
            rows={2}
            name="dish_shorttext"
            value={formData.dish_shorttext}
            onChange={handleFormChange}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Полный Рецепт</Form.Label>
          <Form.Control
            as="textarea"
            rows={5}
            name="recipe_fulltext"
            value={formData.recipe_fulltext}
            onChange={handleFormChange}
          />
        </Form.Group>
        <Form.Group className="mb-3">
          <Form.Label>Категории (через запятую)</Form.Label>
          <Form.Control
            type="text"
            name="categories"
            value={formData.categories}
            onChange={handleFormChange}
          />
        </Form.Group>

        <h4 className="mt-4">Ингредиенты Рецепта</h4>
        {currentRecipePositions.map((pos, index) => (
          <Row key={pos.id} className="mb-2 align-items-end">
            <Col md={4}>
              <Form.Group>
                <Form.Label>Ингредиент</Form.Label>
                <Form.Select
                  name="ingredient_id"
                  value={pos.ingredient_id}
                  onChange={(e) => handleRecipePositionChange(index, e)}
                >
                  <option value={0}>Выберите ингредиент</option>
                  {availableIngredients.map((ing) => (
                    <option key={ing.id} value={ing.id}>
                      {ing.name}
                    </option>
                  ))}
                </Form.Select>
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>Количество</Form.Label>
                <Form.Control
                  type="number"
                  name="amount"
                  value={pos.amount}
                  onChange={(e) => handleRecipePositionChange(index, e)}
                />
              </Form.Group>
            </Col>
            <Col md={3}>
              <Form.Group>
                <Form.Label>Единица измерения</Form.Label>
                <Form.Control
                  type="text"
                  name="unit"
                  value={pos.unit}
                  onChange={(e) => handleRecipePositionChange(index, e)}
                />
              </Form.Group>
            </Col>
            <Col md={2}>
              <Form.Group>
                <Form.Label>Примечания</Form.Label>
                <Form.Control
                  type="text"
                  name="notes"
                  value={pos.notes}
                  onChange={(e) => handleRecipePositionChange(index, e)}
                />
              </Form.Group>
            </Col>
            <Col md={1}>
              <Button variant="danger" size="sm" onClick={() => handleRemoveRecipePosition(pos.id)}>
                X
              </Button>
            </Col>
          </Row>
        ))}
        <Button variant="secondary" size="sm" onClick={handleAddRecipePosition} className="mt-2">
          Добавить Ингредиент в Рецепт
        </Button>

        <div className="mt-4">
          <Button variant="primary" type="submit" className="me-2">
            Сохранить
          </Button>
          <Button variant="secondary" onClick={() => navigate('/recipes')}>
            Отмена
          </Button>
        </div>
      </Form>
    </Container>
  );
};

export default RecipeForm;
