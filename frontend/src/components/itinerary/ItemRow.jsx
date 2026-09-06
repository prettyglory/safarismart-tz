import { MapPin, Bed, Bus, Route as RouteIcon, Utensils, Info } from 'lucide-react';
import { formatRange } from '../../utils/format';

const ICONS = {
  ATTRACTION: MapPin,
  ACCOMMODATION: Bed,
  TRANSPORT: Bus,
  ROUTE: RouteIcon,
  RESTAURANT: Utensils,
  NOTE: Info,
};

export default function ItemRow({ item }) {
  const Icon = ICONS[item.type] || Info;
  const costLabel = formatRange(item.costMin, item.costMax);

  return (
    <div className={`item-row item-row--${item.type.toLowerCase()}`}>
      <Icon size={18} className="item-row__icon" />
      <div className="item-row__body">
        <div className="item-row__top">
          <span className="item-row__name">{item.name}</span>
          {costLabel && <span className="item-row__cost">{costLabel}</span>}
        </div>
        {item.notes && <p className="item-row__notes">{item.notes}</p>}
      </div>
    </div>
  );
}
