import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Layout from './ui/Layout';
import Home from '../pages/home/Home';
import Privacy from '../pages/privacy/Privacy';

export default function App() {
  return <BrowserRouter>
    <Routes>
      <Route path='/' element={<Layout />}>
        <Route index element={<Home />} />
        <Route path='privacy' element={<Privacy />} />
      </Route>
    </Routes>
  </BrowserRouter>;
}