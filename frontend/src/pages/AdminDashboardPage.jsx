import { useEffect, useState } from 'react';
import CrudPanel from '../components/admin/CrudPanel';
import UsersPanel from '../components/admin/UsersPanel';
import * as adminApi from '../api/adminApi';

const TABS = [
  'Regions', 'Destinations', 'Attractions', 'Accommodations',
  'Transport', 'Routes', 'Restaurants', 'Guidelines', 'Users',
];

const TRAVEL_STYLES = ['BUDGET', 'MODERATE', 'LUXURY'];
const TRANSPORT_TYPES = ['BUS', 'FLIGHT', 'FERRY', 'PRIVATE_CAR', 'TRAIN', 'OTHER'];

export default function AdminDashboardPage() {
  const [activeTab, setActiveTab] = useState('Regions');
  const [regions, setRegions] = useState([]);
  const [destinations, setDestinations] = useState([]);

  // Reference data used across several panels' "select" fields.
  useEffect(() => {
    adminApi.listRegions().then(setRegions).catch(() => {});
    adminApi.listDestinations().then(setDestinations).catch(() => {});
  }, [activeTab]);

  const referenceData = { regions, destinations };

  return (
    <div className="admin-dashboard">
      <h1>Admin dashboard</h1>
      <nav className="admin-tabs">
        {TABS.map((tab) => (
          <button
            key={tab}
            className={`admin-tabs__item ${activeTab === tab ? 'admin-tabs__item--active' : ''}`}
            onClick={() => setActiveTab(tab)}
          >
            {tab}
          </button>
        ))}
      </nav>

      <div className="admin-panel-wrapper">
        {activeTab === 'Regions' && (
          <CrudPanel
            title="Regions"
            fields={[{ name: 'name', label: 'Name', type: 'text', required: true }]}
            columns={[{ key: 'id', label: 'ID' }, { key: 'name', label: 'Name' }]}
            api={{
              list: adminApi.listRegions,
              create: adminApi.createRegion,
              update: adminApi.updateRegion,
              remove: adminApi.deleteRegion,
            }}
          />
        )}

        {activeTab === 'Destinations' && (
          <CrudPanel
            title="Destinations"
            fields={[
              { name: 'regionId', label: 'Region', type: 'select', optionsKey: 'regions', required: true,
                accessor: (item) => item.region?.id },
              { name: 'name', label: 'Name', type: 'text', required: true },
              { name: 'description', label: 'Description', type: 'textarea' },
              { name: 'latitude', label: 'Latitude', type: 'number' },
              { name: 'longitude', label: 'Longitude', type: 'number' },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'name', label: 'Name' },
              { key: 'region', label: 'Region', render: (item) => item.region?.name },
            ]}
            api={{
              list: adminApi.listDestinations,
              create: adminApi.createDestination,
              update: adminApi.updateDestination,
              remove: adminApi.deleteDestination,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Attractions' && (
          <CrudPanel
            title="Attractions"
            fields={[
              { name: 'destinationId', label: 'Destination', type: 'select', optionsKey: 'destinations', required: true,
                accessor: (item) => item.destination?.id },
              { name: 'name', label: 'Name', type: 'text', required: true },
              { name: 'category', label: 'Category', type: 'text', required: true },
              { name: 'interestTags', label: 'Interest tags (comma-separated)', type: 'tags',
                accessor: (item) => (item.interestTags || []).join(', ') },
              { name: 'description', label: 'Description', type: 'textarea' },
              { name: 'entranceFeeMin', label: 'Entrance fee (min)', type: 'number', required: true },
              { name: 'entranceFeeMax', label: 'Entrance fee (max)', type: 'number', required: true },
              { name: 'avgDurationHours', label: 'Avg. duration (hours)', type: 'number', required: true },
              { name: 'communityBased', label: 'Community-based', type: 'checkbox' },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'name', label: 'Name' },
              { key: 'destination', label: 'Destination', render: (item) => item.destination?.name },
              { key: 'category', label: 'Category' },
              { key: 'active', label: 'Active', render: (item) => (item.active ? 'Yes' : 'No') },
            ]}
            api={{
              list: adminApi.listAttractions,
              create: adminApi.createAttraction,
              update: adminApi.updateAttraction,
              setActive: adminApi.setAttractionActive,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Accommodations' && (
          <CrudPanel
            title="Accommodations"
            fields={[
              { name: 'destinationId', label: 'Destination', type: 'select', optionsKey: 'destinations', required: true,
                accessor: (item) => item.destination?.id },
              { name: 'name', label: 'Name', type: 'text', required: true },
              { name: 'style', label: 'Style', type: 'select', staticOptions: TRAVEL_STYLES, required: true },
              { name: 'priceMin', label: 'Price/night (min)', type: 'number', required: true },
              { name: 'priceMax', label: 'Price/night (max)', type: 'number', required: true },
              { name: 'description', label: 'Description', type: 'textarea' },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'name', label: 'Name' },
              { key: 'destination', label: 'Destination', render: (item) => item.destination?.name },
              { key: 'style', label: 'Style' },
              { key: 'active', label: 'Active', render: (item) => (item.active ? 'Yes' : 'No') },
            ]}
            api={{
              list: adminApi.listAccommodations,
              create: adminApi.createAccommodation,
              update: adminApi.updateAccommodation,
              setActive: adminApi.setAccommodationActive,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Transport' && (
          <CrudPanel
            title="Local transport options"
            fields={[
              { name: 'destinationId', label: 'Destination', type: 'select', optionsKey: 'destinations', required: true,
                accessor: (item) => item.destination?.id },
              { name: 'type', label: 'Type', type: 'select', staticOptions: TRANSPORT_TYPES, required: true },
              { name: 'description', label: 'Description', type: 'textarea' },
              { name: 'priceMin', label: 'Price (min)', type: 'number', required: true },
              { name: 'priceMax', label: 'Price (max)', type: 'number', required: true },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'destination', label: 'Destination', render: (item) => item.destination?.name },
              { key: 'type', label: 'Type' },
              { key: 'active', label: 'Active', render: (item) => (item.active ? 'Yes' : 'No') },
            ]}
            api={{
              list: adminApi.listTransportOptions,
              create: adminApi.createTransportOption,
              update: adminApi.updateTransportOption,
              setActive: adminApi.setTransportOptionActive,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Routes' && (
          <CrudPanel
            title="Inter-destination routes"
            fields={[
              { name: 'fromDestinationId', label: 'From', type: 'select', optionsKey: 'destinations', required: true,
                accessor: (item) => item.fromDestination?.id },
              { name: 'toDestinationId', label: 'To', type: 'select', optionsKey: 'destinations', required: true,
                accessor: (item) => item.toDestination?.id },
              { name: 'type', label: 'Type', type: 'select', staticOptions: TRANSPORT_TYPES, required: true },
              { name: 'estimatedDurationHours', label: 'Duration (hours)', type: 'number', required: true },
              { name: 'priceMin', label: 'Price (min)', type: 'number', required: true },
              { name: 'priceMax', label: 'Price (max)', type: 'number', required: true },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'from', label: 'From', render: (item) => item.fromDestination?.name },
              { key: 'to', label: 'To', render: (item) => item.toDestination?.name },
              { key: 'type', label: 'Type' },
              { key: 'active', label: 'Active', render: (item) => (item.active ? 'Yes' : 'No') },
            ]}
            api={{
              list: adminApi.listRoutes,
              create: adminApi.createRoute,
              update: adminApi.updateRoute,
              setActive: adminApi.setRouteActive,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Restaurants' && (
          <CrudPanel
            title="Restaurants"
            fields={[
              { name: 'destinationId', label: 'Destination', type: 'select', optionsKey: 'destinations', required: true,
                accessor: (item) => item.destination?.id },
              { name: 'name', label: 'Name', type: 'text', required: true },
              { name: 'cuisineType', label: 'Cuisine type', type: 'text' },
              { name: 'priceRange', label: 'Price range', type: 'select', staticOptions: TRAVEL_STYLES, required: true },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'name', label: 'Name' },
              { key: 'destination', label: 'Destination', render: (item) => item.destination?.name },
              { key: 'priceRange', label: 'Price range' },
              { key: 'active', label: 'Active', render: (item) => (item.active ? 'Yes' : 'No') },
            ]}
            api={{
              list: adminApi.listRestaurants,
              create: adminApi.createRestaurant,
              update: adminApi.updateRestaurant,
              setActive: adminApi.setRestaurantActive,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Guidelines' && (
          <CrudPanel
            title="Responsible tourism guidelines"
            fields={[
              { name: 'regionId', label: 'Region (optional)', type: 'select', optionsKey: 'regions', nullable: true,
                accessor: (item) => item.region?.id },
              { name: 'destinationId', label: 'Destination (optional)', type: 'select', optionsKey: 'destinations', nullable: true,
                accessor: (item) => item.destination?.id },
              { name: 'category', label: 'Category', type: 'text', required: true },
              { name: 'guidelineText', label: 'Guideline text', type: 'textarea', required: true },
            ]}
            columns={[
              { key: 'id', label: 'ID' },
              { key: 'category', label: 'Category' },
              { key: 'scope', label: 'Scope', render: (item) => item.destination?.name || item.region?.name || 'General' },
            ]}
            api={{
              list: adminApi.listGuidelines,
              create: adminApi.createGuideline,
              update: adminApi.updateGuideline,
              remove: adminApi.deleteGuideline,
            }}
            referenceData={referenceData}
          />
        )}

        {activeTab === 'Users' && <UsersPanel />}
      </div>
    </div>
  );
}
