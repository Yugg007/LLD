# Low-Level Design (LLD) Practice

A collection of object-oriented **Low-Level Design** implementations of popular system-design interview problems. Each project focuses on clean class design, separation of concerns, SOLID principles, and well-known design patterns.

## Projects

| Project | Description | Path |
| --- | --- | --- |
| **BookMyShow** | Movie ticket booking system: theatres, screens, shows, seat selection and booking | [`BookMyShow/src`](./BookMyShow/src) |
| **Food Delivery System** | Online food ordering: restaurants, menus, carts, orders and delivery tracking | [`FoodDeliverySystem/src`](./FoodDeliverySystem/src) |
| **Library Management System** | Managing books, members, issue/return flow and fines | [`LibraryManagementSystem/com/lld/lbm`](./LibraryManagementSystem/com/lld/lbm) |
| **Parking Lot System** | Multi-level parking: vehicle types, slot allocation, ticketing and payment | [`ParkingLotSystem/src`](./ParkingLotSystem/src) |
| **Splitwise** | Expense sharing: groups, splits (equal/exact/percent), balances and settlements | [`SplitWise/src`](./SplitWise/src) |

## Repository Structure

```
LLD/
├── BookMyShow/
│   └── src/
├── FoodDeliverySystem/
│   └── src/
├── LibraryManagementSystem/
│   └── com/lld/lbm/
├── ParkingLotSystem/
│   └── src/
├── SplitWise/
│   └── src/
└── .gitignore
```

## Concepts Covered

- Object-oriented design (encapsulation, inheritance, polymorphism, abstraction)
- SOLID principles
- Common design patterns (e.g. Strategy, Factory, Singleton, Observer)
- Entity modelling and relationships between classes
- Extensible, maintainable code structure

> Update this list to match the patterns you actually used in each project.

## Getting Started

### Prerequisites

- JDK 11 or higher
- Any Java IDE (IntelliJ IDEA, Eclipse, VS Code) or just the command line

### Clone

```bash
git clone https://github.com/Yugg007/LLD.git
cd LLD
```

### Run a project (command line)

Each project is self-contained. For example, to run Parking Lot System:

```bash
cd ParkingLotSystem/src
javac *.java
java Main
```

> Adjust the entry-point class name (`Main`) and package paths per project. For `LibraryManagementSystem`, compile from the project root since it uses the `com.lld.lbm` package:
>
> ```bash
> cd LibraryManagementSystem
> javac com/lld/lbm/*.java
> java com.lld.lbm.Main
> ```

### Run in an IDE

1. Open the repository folder (or an individual project folder) in your IDE.
2. Mark the `src` folder as the source root.
3. Run the class containing the `main` method.

## How Each Project Is Approached

1. **Requirements**: clarify functional and non-functional requirements.
2. **Entities**: identify core classes and their responsibilities.
3. **Relationships**: define associations, composition and inheritance.
4. **Patterns**: apply suitable design patterns where they add value.
5. **Driver code**: demonstrate the main flows via a `Main` class.

## Contributing

Suggestions and improvements are welcome.

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit your changes (`git commit -m "Add your feature"`)
4. Push the branch (`git push origin feature/your-feature`)
5. Open a Pull Request

## Author

**Yugg007**: [GitHub](https://github.com/Yugg007)
