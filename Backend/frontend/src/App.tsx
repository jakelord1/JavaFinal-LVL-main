import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import Sidebar from './components/Sidebar';
import Recipes from './pages/Recipes';
import Ingredients from './pages/Ingredients';
import RecipeForm from './pages/RecipeForm';

function App() {
  return (
    <Router>
      <div className="d-flex">
        <Sidebar />
        <div className="content">
          <Routes>
            <Route path="/homecook/recipes" element={<Recipes />} />
            <Route path="/homecook/ingredients" element={<Ingredients />} />
            <Route path="/homecook/recipe/new" element={<RecipeForm />} />
            <Route path="/homecook/recipe/:id" element={<RecipeForm />} />
            <Route path="/homecook/" element={<Recipes />} />
          </Routes>
        </div>
      </div>
    </Router>
  );
}

export default App;
