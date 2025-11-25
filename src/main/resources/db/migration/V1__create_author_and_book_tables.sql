CREATE TABLE author (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    surname VARCHAR(255) NOT NULL,
    bio TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL
);

CREATE TABLE book (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    title VARCHAR(255) NOT NULL,
    isbn VARCHAR(13) NOT NULL UNIQUE,
    published_year INTEGER NOT NULL,
    author_id UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_author FOREIGN KEY (author_id) REFERENCES author(id)
);

CREATE INDEX idx_book_title ON book(title);
CREATE INDEX idx_book_isbn ON book(isbn);
CREATE INDEX idx_book_published_year ON book(published_year);
CREATE INDEX idx_author_name ON author(name);
CREATE INDEX idx_author_surname ON author(surname);
