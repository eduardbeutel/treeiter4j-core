# treeiter4j-core

[![Build Status](https://travis-ci.org/eduardbeutel/treeiter4j-core.svg?branch=master)](https://travis-ci.org/eduardbeutel/treeiter4j-core)
[![Coverage Status](https://coveralls.io/repos/github/eduardbeutel/treeiter4j-core/badge.svg?branch=master)](https://coveralls.io/github/eduardbeutel/treeiter4j-core?branch=master)

treeiter4j-core is a Java implementation of the [Tree Iterator](https://github.com/eduardbeutel/treeiter) pattern that has no dependencies.
It provides the following tree iterators:

- **ElementTreeIterator** for iterating XMLs (org.w3c.dom.Document) on Element level
- **AbstractDocumentTreeIterator** as a base for building other document tree iterators
    
Example: remove an XML element by id

    ElementTreeIterator.topDown(document)
            .whenId("book").remove()
            .execute()
    ;
   