# Agence Voyage - Projet Test Logiciel

Laora Aimi, Maud Genetet & Jules Maulard

Ce projet est une application de planification de voyages développée dans le cadre du module de Test Logiciel (I3GL - IT306). L'objectif principal est la mise en œuvre de pratiques de test avancées : tests unitaires, tests d'intégration, couverture de code et tests de mutation.


## Configuration

Créez un fichier `.env` à la racine du projet (copiez `.env.example`) et ajoutez votre clé API pour geocode.maps.co.


## Commandes de Test

### Tests Unitaires

```bash
mvn clean test
```

### Tests d'Intégration

```bash
mvn clean integration-test
```

### Rapport de Couverture (JaCoCo)

```bash
mvn clean test jacoco:report
```
Ouvrir : `target/site/jacoco/index.html`

### Tests de Mutation (Pitest)

```bash
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage
```
Ouvrir : `target/pit-reports/index.html`