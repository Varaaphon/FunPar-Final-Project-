# FunPar-Final-Project-
Functional and Parallel Programming Project 
Project Description: SPWS - Scala Parallel Web Scraper
# What is the Project?
The Scala Parallel Web Scraper (SPWS) is a project I've been working on to fetch, process, and 
categorize research articles from academic journals or repositories using web scraping techniques. 
The idea is to use functional and parallel programming concepts to efficiently gather and analyze data. 

 # Concept of Functional & Parallel Programming applied : 

Functional Programming:

Immutability: Ensured thread-safety and easier debugging by keeping data structures unchangeable.

Higher-Order Functions: Enhanced code reusability and abstraction.

Lazy Evaluation: Optimized resource usage by deferring computation.

Pattern Matching: Used extensively for data extraction and branching logic.

Parallel Programming:

Parallel Collections: Allowed data processing in parallel with minimal code changes.

Future and Promise: Managed asynchronous computations effectively.

# How to Evaluate the Project
To determine the success of the project, I will consider the following evaluation criteria:

Functionality:

Scraper correctly fetch and parse the articles from the specified sources
The articles accurately categorized based on the defined criteria

Performance:

Measure the time taken to scrape a predefined number of articles.
Assess the efficiency of data processing and categorization.

Scalability:

Evaluate how the system performs with increasing amounts of data.
Test the ability to handle multiple sources and larger datasets concurrently.

# How to Increase Efficiency and Runtime of the Project
To improve the efficiency and runtime of the project, I plan to:


Enhance Web Scraping Logic:

Implement more sophisticated scraping techniques to handle complex web structures.
Use batching and rate limiting to avoid overloading web servers and getting blocked.

Benchmarking and Profiling:

Regularly benchmark the system to identify bottlenecks and optimize performance.
Profile the code to pinpoint and resolve performance issues.

# To run the project :
Download and unzip the SPWS folder 

To run the parallel version : sbt "runMain ParallelWebScraper"

To run the sequential version : sbt "runMain SequentialWebScraper"

Test the performance between the two version : sbt "runMain Benchmark" 

if you are using "input.txt" please switch to "input2.txt"


