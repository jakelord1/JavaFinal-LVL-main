import { Link, Outlet } from "react-router-dom";
import './Layout.css';

export default function Layout() {
    return <>
        <header>
            <nav className="navbar navbar-expand-lg bg-body-tertiary border-bottom">
                <div className="container-fluid">
                    <Link className="navbar-brand" to="/">Frontend-222</Link>
                    <button className="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                        <span className="navbar-toggler-icon"></span>
                    </button>
                    <div className="collapse navbar-collapse" id="navbarSupportedContent">
                        <div className="d-flex justify-content-between w-100">
                            <ul className="navbar-nav mb-2 mb-lg-0">
                                <li className="nav-item">
                                    <Link className="nav-link active" to="/">Home</Link>
                                </li>
                                <li className="nav-item">
                                    <Link className="nav-link active" to="/privacy">Privacy</Link>
                                </li>
                            </ul>
                            <form className="d-flex" role="search" onSubmit={e => e.preventDefault()}>
                                <input className="form-control me-2" type="search" placeholder="Search" aria-label="Search" />
                                <button className="btn btn-outline-success" type="submit">Search</button>
                            </form>
                            <div>
                                <button className="btn btn-outline-secondary" data-bs-toggle="modal" data-bs-target="#authModal">
                                    <i class="bi bi-box-arrow-in-right"></i>
                                </button>
                            </div>
                        </div>
                    </div>
                </div>
            </nav>
        </header>
        <main>
            <div className="container">
                <Outlet />
            </div>
        </main>
        <footer className="bg-body-tertiary border-top py-3">
            <div className="container">
                &copy; 2025 - Phen9men Inc.
            </div>
        </footer>


        <div class="modal fade" id="authModal" tabIndex="-1" aria-labelledby="authModalLabel" aria-hidden="true">
            <div class="modal-dialog">
                <div class="modal-content">
                    <div class="modal-header">
                        <h1 class="modal-title fs-5" id="authModalLabel">Вхід до сайту</h1>
                        <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <div class="input-group mb-3">
                            <span class="input-group-text" id="basic-addon1">@</span>
                            <input type="text" class="form-control" placeholder="Username" aria-label="Username" aria-describedby="basic-addon1">
                        </div>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Скасувати</button>
                        <button type="button" class="btn btn-primary">Вхід</button>
                    </div>
                </div>
            </div>
        </div>
    </>;
}