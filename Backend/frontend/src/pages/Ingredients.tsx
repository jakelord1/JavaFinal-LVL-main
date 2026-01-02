import { useState, useEffect } from 'react';
import { Button, Table, Modal, Form, Card } from 'react-bootstrap';
import { type Ingredient } from '../data/mockData';

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost';

const Ingredients = () => {
  const [ingredients, setIngredients] = useState<Ingredient[]>([]);
  const [showModal, setShowModal] = useState(false);
  const [selectedIngredient, setSelectedIngredient] = useState<Ingredient | null>(null);

useEffect(() => {
    fetch(`${API_URL}:8080/homecook/ingredients`, {
      method: 'GET',
    })
      .then((res) => {
        if (!res.ok) throw new Error('Failed to fetch ingredients');
        return res.json();
      })
      .then((data: Ingredient[]) => setIngredients(data))
      .catch((err) => console.error(err));
  }, []);


  const handleClose = () => {
    setShowModal(false);
    setSelectedIngredient(null);
  };

  const handleShow = (ingredient?: Ingredient) => {
    setSelectedIngredient(ingredient || null);
    setShowModal(true);
  };

  const handleDelete = (id: number) => {
      if (window.confirm('Are you sure you want to delete this ingredient?')) {
        fetch(`${API_URL}:8080/homecook/ingredients?id=${id}`, {
          method: 'DELETE',
        })
          .then((res) => {
            if (!res.ok) throw new Error('Failed to delete ingredient');
            setIngredients((prev) => prev.filter((ing) => ing.id !== id));
          })
          .catch((err) => console.error(err));
      };
      
    };

    const handleSave = (ingredientData: Omit<Ingredient, 'id'> & { id?: number }) => {
        if (ingredientData.id) {
        // update
        fetch(`${API_URL}:8080/homecook/ingredients?id=${ingredientData.id}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(ingredientData),
        })
          .then((res) => {
            if (!res.ok) throw new Error('Failed to update ingredient');
            return res.json();
          })
          .then((updated: Ingredient) => {
            setIngredients((prev) =>
              prev.map((ing) => (ing.id === updated.id ? updated : ing))
            );
            handleClose();
          })
          .catch((err) => console.error(err));
      } else {
        // create
        fetch(`${API_URL}:8080/homecook/ingredients`, {
          method: 'POST',
          headers: { 'Content-Type': 'application/json' },
          body: JSON.stringify(ingredientData),
        })
          .then((res) => {
            if (!res.ok) throw new Error('Failed to create ingredient');
            return res.json();
          })
          .then((created: Ingredient) => {
            setIngredients((prev) => [...prev, created]);
            handleClose();
          })
          .catch((err) => console.error(err));
      }
    };

  return (
    <div>
      <div className="d-flex justify-content-between align-items-center mb-4">
        <h2>Управление Ингредиентами</h2>
        <Button variant="primary" onClick={() => handleShow()}>
          <i className="bi bi-plus-lg me-2"></i>
          Добавить Ингредиент
        </Button>
      </div>

      <Card>
        <Card.Body>
          <Table striped bordered hover responsive>
            <thead>
              <tr>
                <th>ID</th>
                <th>Название</th>
                <th>Категория</th>
                <th>Описание</th>
                <th>Действия</th>
              </tr>
            </thead>
            <tbody>
              {ingredients.map((ingredient) => (
                <tr key={ingredient.id}>
                  <td>{ingredient.id}</td>
                  <td>{ingredient.name}</td>
                  <td>{ingredient.category}</td>
                  <td>{ingredient.description}</td>
                  <td>
                    <Button variant="outline-primary" size="sm" onClick={() => handleShow(ingredient)}>
                      <i className="bi bi-pencil-fill"></i>
                    </Button>{' '}
                    <Button variant="outline-danger" size="sm" onClick={() => handleDelete(ingredient.id)}>
                      <i className="bi bi-trash-fill"></i>
                    </Button>
                  </td>
                </tr>
              ))}
            </tbody>
          </Table>
        </Card.Body>
      </Card>

      <IngredientModal
        show={showModal}
        handleClose={handleClose}
        handleSave={handleSave}
        ingredient={selectedIngredient}
      />
    </div>
  );
  };

interface IngredientModalProps {
  show: boolean;
  handleClose: () => void;
  handleSave: (ingredientData: Omit<Ingredient, 'id'> & { id?: number }) => void;
  ingredient: Ingredient | null;
}

const IngredientModal = ({ show, handleClose, handleSave, ingredient }: IngredientModalProps) => {
  const [formData, setFormData] = useState<Omit<Ingredient, 'id'>>({
    name: '',
    category: '',
    description: '',
  });

  useEffect(() => {
    if (ingredient) {
      setFormData(ingredient);
    } else {
      setFormData({ name: '', category: '', description: '' });
    }
  }, [ingredient, show]);


  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    handleSave({ ...formData, id: ingredient?.id });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };


  return (
    <Modal show={show} onHide={handleClose} centered>
      <Modal.Header closeButton>
        <Modal.Title>{ingredient ? 'Редактировать' : 'Добавить'} Ингредиент</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3">
            <Form.Label>Название</Form.Label>
            <Form.Control
              type="text"
              name="name"
              value={formData.name}
              onChange={handleChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Категория</Form.Label>
            <Form.Control
              type="text"
              name="category"
              value={formData.category}
              onChange={handleChange}
              required
            />
          </Form.Group>
          <Form.Group className="mb-3">
            <Form.Label>Описание</Form.Label>
            <Form.Control
              as="textarea"
              rows={3}
              name="description"
              value={formData.description}
              onChange={handleChange}
            />
          </Form.Group>
          <div className="d-flex justify-content-end">
            <Button variant="secondary" onClick={handleClose} className="me-2">
              Отмена
            </Button>
            <Button variant="primary" type="submit">
              Сохранить
            </Button>
          </div>
        </Form>
      </Modal.Body>
    </Modal>
  );
};

export default Ingredients;
