import { Link } from 'react-router-dom';

export default function HomePage() {
  return (
    <section className="hero">
      <div className="hero__content">
        <h1>SafariSmart TZ</h1>

        <p>
          A Tanzania tourism web application built with React and Spring Boot.
        </p>

        <Link
          to="/register"
          className="btn btn--primary btn--large"
        >
          Get Started
        </Link>
      </div>
    </section>
  );
}