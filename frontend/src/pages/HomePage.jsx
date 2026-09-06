import { Link } from 'react-router-dom';

export default function HomePage() {
  return (
    <section className="hero">
      <div className="hero__content">
        <h1>Tanzania, planned around what you actually want to do.</h1>
        <p>
          Tell us your days, your budget, and what draws you here — wildlife, beaches,
          mountains, culture — and we'll build a day-by-day plan from verified local data,
          not guesses.
        </p>
        <Link to="/plan" className="btn btn--primary btn--large">Start planning</Link>
      </div>
    </section>
  );
}
