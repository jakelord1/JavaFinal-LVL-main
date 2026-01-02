import { NavLink } from 'react-router-dom';
import './Sidebar.css';

const Sidebar = () => {
  return (
    <nav className="sidebar">
      <div className="sidebar-header">
        <NavLink to="/homecook/" className="logo">
          <svg width="28" height="28" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg" className="logo-icon">
            <path d="M12 2C6.48 2 2 6.48 2 12C2 17.52 6.48 22 12 22C17.52 22 22 17.52 22 12C22 6.48 17.52 2 12 2ZM12 20C7.59 20 4 16.41 4 12C4 7.59 7.59 4 12 4C16.41 4 20 7.59 20 12C20 16.41 16.41 20 12 20Z" fill="currentColor"/>
            <path d="M12 6C8.69 6 6 8.69 6 12C6 15.31 8.69 18 12 18C15.31 18 18 15.31 18 12C18 8.69 15.31 6 12 6ZM12 16C9.79 16 8 14.21 8 12C8 9.79 9.79 8 12 8C14.21 8 16 9.79 16 12C16 14.21 14.21 16 12 16Z" fill="currentColor"/>
          </svg>
          <span className="logo-text">HomeCook</span>
        </NavLink>
      </div>
      
      <div className="sidebar-menu">
        <NavItem to="/homecook/recipes" icon={
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M19 3H5C3.9 3 3 3.9 3 5V19C3 20.1 3.9 21 5 21H19C20.1 21 21 20.1 21 19V5C21 3.9 20.1 3 19 3ZM19 19H5V5H19V19Z" fill="currentColor"/>
            <path d="M7 7H17V9H7V7ZM7 11H17V13H7V11ZM7 15H14V17H7V15Z" fill="currentColor"/>
          </svg>
        }>Recipes</NavItem>
        
        <NavItem to="/homecook/ingredients" icon={
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" xmlns="http://www.w3.org/2000/svg">
            <path d="M8.1 13.34L10.93 16.17L15.9 11.2C16.28 10.82 16.28 10.2 15.9 9.82L13.07 7L8.1 11.97V13.34ZM19.04 8.71C19.62 8.13 19.62 7.18 19.03 6.6L17.42 5C16.84 4.42 15.88 4.42 15.3 5.01L14.49 5.82L17.17 8.5L19.04 8.71Z" fill="currentColor"/>
            <path d="M11 20H5C3.9 20 3 19.1 3 18V6C3 5.59 3.35 5.25 3.75 5.25C4.16 5.25 4.5 5.59 4.5 6V18C4.5 18.27 4.73 18.5 5 18.5H11C11.41 18.5 11.75 18.84 11.75 19.25C11.75 19.66 11.41 20 11 20Z" fill="currentColor"/>
          </svg>
        }>Ingredients</NavItem>
      </div>
    </nav>
  );
};

const NavItem = ({ to, icon, children }: { to: string; icon: React.ReactNode; children: React.ReactNode }) => (
  <NavLink 
    to={to} 
    className={({ isActive }) => `nav-item ${isActive ? 'active' : ''}`}
  >
    <span className="nav-icon">{icon}</span>
    <span className="nav-text">{children}</span>
  </NavLink>
);

export default Sidebar;
