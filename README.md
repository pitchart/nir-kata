# Bulletproof your code with "Mutation-Based Property-Driven Development"
This repository is a fork to be used during `Software Craft Luxembourg Meetup`.

![NIR kata @SCL](img/bulletproof-your-code.png)

### Disclaimer
Original concept has been presented by [Arnaud Bailly](https://www.linkedin.com/in/arnaudbailly/) and documented [here](https://abailly.github.io/about.html).

- [The kata](#the-kata)
  - [NIR rules](#nir-rules) 
  - [Examples](#examples)
- [1) Validate a NIR (String)](#1-validate-a-nir-string)
  - [Finalize the implementation 🫵](#finalize-the-implementation-)
  - [Limit of validate approach](#limit-of-validate-approach)
- [2) Fight Primitive Obsession](#2-fight-primitive-obsession)
  - [How to](#how-to)
  - [Create the Roundtrip property](#create-the-roundtrip-property)
  - [Type-Driven Development](#type-driven-development)
  - [Design the Year type](#design-the-year-type)
  - [Fast Forward the design of other types](#fast-forward-the-design-of-other-types)
- [3) Bulletproof your code with "Mutation-based Property-Driven Development"](#3-bulletproof-your-code-with-mutation-based-property-driven-development)
  - [Create a Sex mutator](#create-a-sex-mutator)
  - [Write a Truncate mutator 🫵](#write-a-truncate-mutator-)
  - [Write other mutators 🫵](#write-other-mutators-)
  - [Advantages of it](#advantages-of-it)
- [4) Reflect](#4-reflect)
- ["Solutions"](#solutions)
- [To go further](#to-go-further)
- [Resources](#resources)

## The kata
> Write a system that can handle `NIR` (simplified rules explained below)

In this hands-on session we will develop together a system that can handle NIR "French social security number" by using / experimenting the following practices:
- `Test-Driven Development` to design a first version of our algorithm (with Example Based approach)
- `Fight Primitive Obsession` with `Parse Don't Validate` and `Monads`
- `Type-Driven Development` to "make impossible states unrepresentable"
- `Property-Based Testing` to drive our development
- `Mutation-Based Properties` to refine our code and identify edge cases

At the end of this code kata you will have understood a different way of designing code that may / will inspire you for your day-to-day life.

Code is available in `java` and `C#` (#sharingiscaring)

### NIR rules
`NIR` stands for "Numéro de sécurité sociale en France" it is a unique id representing an individual composed by 15 characters.

Here are the simplified specifications you will use for this kata:

| Positions  | Meaning                                                                                         | Possible values                     |
|------------|-------------------------------------------------------------------------------------------------|-------------------------------------|
| 1          | Sex : 1 for men, 2 for women                                                                    | 1 or 2                              |
| 2, 3       | Last two digits of the year of birth (which gives the year to the nearest century)              | From 00 to 99                       |
| 4, 5       | Birth month                                                                                     | From 01 (January) to 12 (December)  |
| 6, 7       | Department of birth                                                                             | From 01 to 95, 99 for births abroad |
| 8, 9, 10   | Official code of the city of birth                                                              | From 001 to 999                     |
| 11, 12, 13 | "Serial number": birth order number in the month and city                                       | From 001 to 999                     |
| 14, 15     | control key = complement to 97 of the number formed by the first 13 digits of the NIR modulo 97 | From 01 to 97                       |

![nir example](img/nir.jpg)

### Examples
Here are some `valid NIRs` regarding those specifications:
- 223115935012322
- 200029923123486
- 254031088723464
- 195017262676215
- 155053933981739
- 106099955391094

And here are some `invalid` ones:
- 2230 // too short
- 323115935012322 // incorrect sex
- 2ab115935012322 // incorrect year
- 223ab5935012322 // incorrect month
- 223145935012322 // incorrect month 2
- 223005935012322 // incorrect month 3
- 22311xx35012322 // incorrect department
- 223119635012322 // incorrect department 2
- 2231159zzz12322 // incorrect city
- 223115935012321 // incorrect control key

## 1) Validate a NIR (String)
- Design a system that can validate if a given `String` is a valid `NIR` number: `String -> Boolean`
- Use Test Driven Development to do so

### Finalize the implementation 🫵
The implementation has already been started using `Test-Driven Development` in the package / namespace `primitive.obsession`.
A test list has been created with our business experts after an [`Example Mapping`](https://xtrem-tdd.netlify.app/Flavours/example-mapping) workshop.

Here is the current status of this list:

```text
Invalid NIRs
✅ empty string
✅ 2230 // too short
✅ 323115935012322 // incorrect sex
✅ 2ab115935012322 // incorrect year
✅ 223ab5935012322 // incorrect month
✅ 223145935012322 // incorrect month 2
✅ 223005935012322 // incorrect month 3
✅ 22311xx35012322 // incorrect department
✅ 223119635012322 // incorrect department 2
✅ 2231159zzz12322 // incorrect city
✅ 2231159123zzz22 // incorrect serial number
✅ 2231159350123221 // too long
-  223115935012321 // incorrect control key


Valid NIRs
- 223115935012322
- 200029923123486
- 254031088723464
- 195017262676215
- 155053933981739
```

- You have to finalize this test list:

```java
class ValidateNIR {
    public static Stream<Arguments> invalidNIRs() {
        return Stream.of(
                Arguments.of("", "empty string"),
                Arguments.of("2230", "too short"),
                Arguments.of("323115935012322", "incorrect sex"),
                Arguments.of("2ab115935012322", "incorrect year"),
                Arguments.of("223ab5935012322", "incorrect month"),
                Arguments.of("223145935012322", "incorrect month 2"),
                Arguments.of("223005935012322", "incorrect month 3"),
                Arguments.of("22311xx35012322", "incorrect department"),
                Arguments.of("223119635012322", "incorrect department 2"),
                Arguments.of("2231159zzz12322", "incorrect city"),
                Arguments.of("2231159350123221", "too long")
                //Arguments.of("223115935012321", "incorrect key") -> work on it
        );
    }
  ...
```

- Use `validNIRs` list to ensure that valid NIRs are validated

> What was the impact of using T.D.D and covering "non passing" tests on the design and implementation?

### Limit of `validate` approach
![Limit of primitive types](solution/docs/img/nir-primitive.png)

Know more about `Primitive Obsession` [here](https://xtrem-tdd.netlify.app/Flavours/no-primitive-types)

## 2) Fight Primitive Obsession
Let's apply ["Parse Don't Validate"](https://xtrem-tdd.netlify.app/Flavours/parse-dont-validate) principle to fight ["Primitive Obsession"](https://xtrem-tdd.netlify.app/Flavours/no-primitive-types).
We will use [`Property Based Testing`](https://xtrem-tdd.netlify.app/Flavours/pbt) in this part of the kata to design our parser.

Our `parsing function` must respect the below property
```text
for all (validNir)
parseNIR(nir.toString) == nir
```

With `parse don't validate` we want to make it impossible to represent an invalid `NIR` in our system. Our data structures need to be `immutables`.

Our parser may look like this: `String -> Either<ParsingError, NIR>`

### How to
- Start with a `parser` that always returns `Right[NIR]`
  - Write a minimalist data structure first (empty one)
- Write a `positive property` test checking valid NIR can be round-tripped
  - Round-tripping: `NIR -> String -> NIR`
    - Assert that round-tripped `NIR` equals original `NIR`
  - To do so, you will have to create your own valid NIR generator
- Write a `negative property` test checking `invalid NIRs` can not be parsed
  - This is where mutations are introduced
  - Each different mutation type representing some possible alteration of the `NIR`
  - Generate invalid NIRs by introducing mutations in the valid ones
- Use the properties to guide your implementation

Inspired by [Arnaud Bailly](https://abailly.github.io/about.html)

### Create the `Roundtrip` property
- Add `vavr-test` to do so

```kotlin
testImplementation("io.vavr:vavr-test:0.10.4")
```

:red_circle: Specify the property

```java
class NIRProperties {
    private Arbitrary<NIR> validNIR = null;

    @Test
    void roundTrip() {
        Property.def("parseNIR(nir.ToString()) == nir")
                .forAll(validNIR)
                .suchThat(nir -> NIR.parse(nir.toString()).contains(nir))
                .check()
                .assertIsSatisfied();
    }
}
```

:green_circle: Make it pass.
- Generate the `NIR` class
- Handle error with a data structure: `ParsingError`

```java
public record ParseError(String message) {
}

@EqualsAndHashCode
public class NIR {
    public static Either<ParsingError, NIR> parse(String input) {
        return right(new NIR());
    }

    @Override
    public String toString() {
        return "";
    }
}

class NIRProperties {
    private Arbitrary<NIR> validNIR = Arbitrary.of(new NIR());

    @Test
    void roundTrip() {
        Property.def("parseNIR(nir.ToString()) == nir") // describe the property
                .forAll(validNIR) // pass an Arbitrary / Generator to generate valid NIRs
                .suchThat(nir -> NIR.parse(nir.toString()).contains(nir)) // describe the Property predicate
                .check()
                .assertIsSatisfied();
    }
}
```

### Type-Driven Development
We will represent the `NIR` with proper immutable types like `Sex`, `Department`... carrying their own parsing logic and rules. 

:large_blue_circle: Create the `Sex` type
- We choose to use an `enum` for that
- It is immutable by design
- We need to work on the `String` representation of it
- Each data structure will contain its own parsing method

```java
public enum Sex {
    M(1), F(2);

    private final int value;

    Sex(int value) {
        this.value = value;
    }

    public static Either<ParsingError, Sex> parseSex(char input) {
        // vavr Pattern matching
        return Match(input).of(
                Case($('1'), right(M)),
                Case($('2'), right(F)),
                Case($(), left((new ParsingError("Not a valid sex"))))
        );
    }

    @Override
    public String toString() {
        return "" + value;
    }
}
```

- Create a generator to be able to generate valid NIRs

```java
private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());
```

- Extend `NIR` with the new created type

```java
@EqualsAndHashCode
public class NIR {
    private final Sex sex;

    public NIR(Sex sex) {
        this.sex = sex;
    }

    public static Either<ParsingError, NIR> parseNIR(String input) {
        return parseSex(input.charAt(0))
                .map(NIR::new);
    }

    @Override
    public String toString() {
        return sex.toString();
    }
}

class NIRProperties {
    private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());
    private final Arbitrary<NIR> validNIR =
            sexGenerator.map(NIR::new)
                    .arbitrary();

    @Test
    void roundTrip() {
        Property.def("parseNIR(nir.ToString()) == nir")
                .forAll(validNIR)
                .suchThat(nir -> NIR.parseNIR(nir.toString()).contains(nir))
                .check()
                .assertIsSatisfied();
    }
}
```

### Design the Year type
Like for the `Sex` type, we design the new type with its generator.

:red_circle: create a generator

```java
private final Gen<Year> yearGenerator = Gen.choose(0, 99).map(Year::fromInt); // have a private constructor
private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());
private final Arbitrary<NIR> validNIR =
        sexGenerator
                .map(NIR::new)
                // use the yearGenerator here
                .arbitrary();
```

:green_circle: To be able to use the `yearGenerator`, we need to have a context to be able to map into it.
It is a mutable data structure that we enrich with the result of each generator. We create a `Builder` class for it:

```java
@With
@Getter
@AllArgsConstructor
public class NIRBuilder {
    private final Sex sex;
    private Year year;

    public NIRBuilder(Sex sex) {
        this.sex = sex;
    }
}
```

- We now adapt the `NIRProperties` to use this `Builder`
```java
class NIRProperties {
    private final Random random = new Random();
    private final Gen<Year> yearGenerator = Gen.choose(0, 99).map(Year::fromInt);
    private final Gen<Sex> sexGenerator = Gen.choose(Sex.values());

    private Arbitrary<NIR> validNIR =
            sexGenerator
                    .map(NIRBuilder::new)
                    .map(builder -> builder.withYear(yearGenerator.apply(random)))
                    .map(x -> new NIR(x.getSex(), x.getYear()))
                    .arbitrary();

    @Test
    void roundTrip() {
        Property.def("parseNIR(nir.ToString()) == nir")
                .forAll(validNIR)
                .suchThat(nir -> NIR.parseNIR(nir.toString()).contains(nir))
                .check()
                .assertIsSatisfied();
    }
}
```

- We now have to adapt the `NIR` class to handle the `Year` in its construct
  - We will use the same `Builder` construct (in other languages we may use `for comprehension` or `LinQ` for example)

```java
@EqualsAndHashCode
@AllArgsConstructor
public class NIR {
    private final Sex sex;
    private final Year year;

    public static Either<ParsingError, NIR> parseNIR(String input) {
        return parseSex(input.charAt(0))
                .map(NIRBuilder::new)
                .flatMap(builder -> right(builder.withYear(new Year(1))))
                .map(builder -> new NIR(builder.getSex(), builder.getYear()));
    }

    @Override
    public String toString() {
        return sex.toString() + year;
    }
}
```

:large_blue_circle: We can now work on the `Year` type and its `parser`

```java
@EqualsAndHashCode
@AllArgsConstructor
public class NIR {
    private final Sex sex;
    private final Year year;

    public static Either<ParsingError, NIR> parseNIR(String input) {
        return parseSex(input.charAt(0))
                .map(NIRBuilder::new)
                .flatMap(builder -> parseYear(input.substring(1, 3), builder))
                .map(builder -> new NIR(builder.getSex(), builder.getYear()));
    }

    private static Either<ParsingError, NIRBuilder> parseYear(String input, NIRBuilder builder) {
        return Year.parseYear(input)
                .map(builder::withYear);
    }

    @Override
    public String toString() {
        return sex.toString() + year;
    }
}

@EqualsAndHashCode
@ExtensionMethod(StringExtensions.class)
public class Year {
    private final int value;

    public Year(int value) {
        this.value = value;
    }

    public static Either<ParsingError, Year> parseYear(String input) {
        return input.toInt()
                .map(Year::new)
                .toEither(new ParsingError("year should be between 0 and 99"));
    }

    public static Year fromInt(Integer x) {
        return parseYear(x.toString())
                .getOrElseThrow(() -> new IllegalArgumentException("Year"));
    }

    @Override
    public String toString() {
        return String.format("%02d", value);
    }
}
```

We can check `Properties` generation by printing the generated nirs:
```text
214
241
240
182
138
294
280
252
158
265
213
225
275
```

### Fast Forward the design of other types
Here are the iterations (You can see their details from the git history):
![Fast forward for Type Driven](solution/docs/img/fast-forward-type-driven.png)

For now:
- It is impossible to represent a `NIR` in an invalid state
- We have a semantic that expresses the concepts behind `NIR`

```java
  @Override
  public String toString() {
      return stringWithoutKey() + format("%02d", key());
  }

  // How the NIR is composed
  private String stringWithoutKey() {
      return sex.toString() + year + month + department + city + serialNumber;
  }
```

> Is it enough designing our types like this?

## 3) Bulletproof your code with "Mutation-based Property-Driven Development"
Let's create a property that demonstrates that an invalid `NIR` can never be parsed.
We will generate a valid one and then mutate its string representation to create an invalid one.
For that we will create some mutators.

```text
for all (validNir)
mutate(nir.toString) == left
```

Some example of mutations:
- `Sex` mutant: a value greater than 2 for example
- `Key` mutant: change the key by using a number between 1 and 97 that does not respect the key definition  

> Which others mutators could you imagine? 🫵

### Create a `Sex` mutator
```java
  private record Mutator(String name, Function1<NIR, Gen<String>> mutate){}

  private static Mutator sexMutator = new Mutator("Sex mutator", nir ->
          Gen.choose(3, 9)
                  .map(invalidSex -> invalidSex + nir.toString().substring(1))
  );
```

- Define the `property` and use the mutator
```java

class NIRMutatedProperties {
    private static final Random random = new Random();

    private record Mutator(String name, Function1<NIR, Gen<String>> func) {
        public String mutate(NIR nir) {
            return func.apply(nir).apply(random);
        }
    }

    private static Mutator sexMutator = new Mutator("Sex mutator", nir ->
            Gen.choose(3, 9)
                    .map(invalidSex -> invalidSex + nir.toString().substring(1))
    );

    private static Arbitrary<Mutator> mutators = Gen.choose(
            sexMutator).arbitrary();

    @Test
    void invalidNIRCanNeverBeParsed() {
        Property.def("parseNIR(nir.ToString()) == nir")
                .forAll(validNIR, mutators)
                .suchThat(NIRMutatedProperties::canNotParseMutatedNIR)
                .check()
                .assertIsSatisfied();
    }

    private static boolean canNotParseMutatedNIR(NIR nir, Mutator mutator) {
        return NIR.parseNIR(mutator.mutate(nir)).isLeft();
    }
}
```

### Write a Truncate mutator 🫵
> What didi you learn from it?

### Write other mutators 🫵
```text
✅ Sex Mutator
✅ Truncate the NIR
Year Mutator
Month Mutator
Department Mutator
City Mutator
Serial Number Mutator
Key Mutator
```

### Advantages of it
![Parsing](solution/docs/img/nir-parse.png)

By using this approach you can mix `T.D.D` with `Type Driven Development`, `Property-Based Testing` and `Mutations` to design extremely robust code.
Indeed, it can help you quickly identify edge cases in your system.

## 4) Reflect
> What did you learn today?
> How could you apply this learning?

![Mutation-based Property-Driven Development](img/mutation-based-property-driven-development.png)

Read more about it [here](https://abailly.github.io/posts/mutation-testing.html)

## "Solutions"
Proposal of solutions are available in the `solution` directory:

- `C#` with `xUnit` | `LanguageExt` | `FsCheck`
- `java` with `jUnit` | `vavr` | `vavr-test`
- `scala 3` with `scalatest` | `scalacheck`
- `F#` with `xUnit` | `FsCheck`
- `kotlin` with `kotest`

A step-by-step guide in `java` is available [here](solution/docs/step-by-step.md)

## To go further
I have created another kata to practice those ideas called `snafu` it is available [here](https://github.com/ythirion/snafu-kata).

## Resources
- [50 shades of TDD (video) by Arnaud Bailly](https://youtu.be/T4SvCLLxTDg)
- [NIR full specification](https://fr.wikipedia.org/wiki/Num%C3%A9ro_de_s%C3%A9curit%C3%A9_sociale_en_France)
- [Online key calculator](http://nourtier.net/cle_NIR/cle_NIR.htm)

