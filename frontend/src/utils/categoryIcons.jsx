import React from 'react';
import {
  FaTools, FaPaintRoller, FaBolt, FaHome, FaUserTie,
  FaCut, FaLeaf, FaDog, FaChalkboardTeacher, FaBroom,
  FaTruck, FaHammer, FaWrench, FaLightbulb, FaSnowflake,
  FaSpa, FaCouch, FaCar, FaCamera, FaMusic, FaBook,
  FaPaw, FaWind, FaGraduationCap, FaStethoscope, FaLaptop,
  FaBug, FaBlender
} from 'react-icons/fa';
import {
  MdPlumbing, MdElectricalServices, MdCleaningServices,
  MdOutlineHomeRepairService, MdPets, MdLocalFlorist,
  MdConstruction, MdHvac, MdLocalShipping, MdBuild,
  MdCarpenter
} from 'react-icons/md';
import {
  GiPaintBrush, GiVacuumCleaner, GiGardeningShears,
  GiHouse, GiWoodPile
} from 'react-icons/gi';

// Map service categories to their respective icons - Updated with all 16 categories
export const categoryIconMap = {
  'Education': { Icon: FaGraduationCap, color: 'from-indigo-500 to-purple-600', bg: 'bg-indigo-50' },
  'Plumbing': { Icon: MdPlumbing, color: 'from-blue-500 to-cyan-600', bg: 'bg-blue-50' },
  'Electrical': { Icon: MdElectricalServices, color: 'from-yellow-500 to-orange-600', bg: 'bg-yellow-50' },
  'Cleaning': { Icon: MdCleaningServices, color: 'from-green-500 to-emerald-600', bg: 'bg-green-50' },
  'Beauty': { Icon: FaSpa, color: 'from-purple-500 to-pink-600', bg: 'bg-purple-50' },
  'IT Support': { Icon: FaLaptop, color: 'from-blue-600 to-indigo-700', bg: 'bg-blue-50' },
  'Home Repair': { Icon: FaTools, color: 'from-gray-600 to-slate-700', bg: 'bg-gray-50' },
  'Health': { Icon: FaStethoscope, color: 'from-red-500 to-pink-600', bg: 'bg-red-50' },
  'Carpentry': { Icon: MdCarpenter, color: 'from-amber-600 to-orange-700', bg: 'bg-amber-50' },
  'Painting': { Icon: FaPaintRoller, color: 'from-pink-500 to-rose-600', bg: 'bg-pink-50' },
  'Appliance Repair': { Icon: FaBlender, color: 'from-teal-500 to-cyan-600', bg: 'bg-teal-50' },
  'Pest Control': { Icon: FaBug, color: 'from-lime-500 to-green-600', bg: 'bg-lime-50' },
  'Moving & Delivery': { Icon: FaTruck, color: 'from-red-500 to-pink-600', bg: 'bg-red-50' },
  'Gardening': { Icon: FaLeaf, color: 'from-green-600 to-lime-600', bg: 'bg-green-50' },
  'HVAC': { Icon: FaWind, color: 'from-sky-500 to-blue-600', bg: 'bg-sky-50' },
  'Automotive': { Icon: FaCar, color: 'from-slate-600 to-gray-700', bg: 'bg-slate-50' },
  'Others': { Icon: MdBuild, color: 'from-gray-500 to-slate-600', bg: 'bg-gray-50' },

  // Legacy fallback support for old category names
  'Tutoring': { Icon: FaGraduationCap, color: 'from-indigo-500 to-purple-600', bg: 'bg-indigo-50' },
  'Beauty & Spa': { Icon: FaSpa, color: 'from-purple-500 to-pink-600', bg: 'bg-purple-50' },
  'Pet Care': { Icon: FaPaw, color: 'from-orange-500 to-red-600', bg: 'bg-orange-50' },
  'Other': { Icon: MdBuild, color: 'from-slate-500 to-gray-600', bg: 'bg-slate-50' }
};

// Get icon component for a category
export const getCategoryIcon = (category) => {
  const iconData = categoryIconMap[category] || categoryIconMap['Other'];
  return iconData;
};

// Service feature icons
export const featureIcons = {
  verified: { Icon: FaUserTie, color: 'from-blue-500 to-indigo-600' },
  fast: { Icon: FaBolt, color: 'from-yellow-500 to-orange-600' },
  quality: { Icon: FaLightbulb, color: 'from-purple-500 to-pink-600' },
  professional: { Icon: FaTools, color: 'from-green-500 to-emerald-600' }
};

export default categoryIconMap;
