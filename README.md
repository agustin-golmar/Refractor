[![...](res/readme-header.png)](https://github.com/agustin-golmar/Refractor)
[![...](https://img.shields.io/badge/Java-v10.0-orange.svg)](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
[![...](https://img.shields.io/badge/release-v0.1-blue.svg)](https://github.com/agustin-golmar/Refractor/releases)
[![...](https://www.travis-ci.com/agustin-golmar/Refractor.svg?branch=master)](https://www.travis-ci.com/agustin-golmar/Refractor)

# Refractor

A user interface to refract images (_i.e._, cutting, filtering, merging,
thresholding, etc.), and extract relevant information from them (like other
images).

Currently, the following features are provided:

* Support for open and save images in whatever format you like: _BMP_, _GIF_, _JPG_, _PGM_, _PNG_, _PPM_, _RAW_, _TIFF_, and _WBMP_.
* Basic unary operations:
	* __Cut__: selects an area with the mouse and duplicates the selection.
	* __Scalar Product__: multiplies an image by a scalar value. Then apply a compressor.
	* __Grayscale__: it takes the bright-component of the _HSB/HSV_ model and make a grayscale of the image.
	* __Negative__: the complement of an image.
* Basic binary operations: _addition_, _subtraction_ and _product_ of images.
* Compressors you can apply after every transformation:
	* __Null Compressor__: it does nothing.
	* __Truncated__: all negative values are set to zero, and all values greater than _255_ are set to _255_.
	* __Linear__: linearly reduces the range of values ​​between _0_ and _255_.
	* __Dynamic-range Compressor__: it applies an automatic logarithmic compression.
* Histogram-based operations:
	* __Grayscale Histogram Chart__: to visualize the distribution of grayscale-colors. You can opt between seeing the pixel count, or the normalized probability.
	* __Histogram Equalization__: an enhancement of the probability distribution that makes the grayscale variance more uniform.
* Thresholding, to make a binary image.
	* __Manual__: selecting a threshold _u_.
	* __Global__: an automatic an effective thresholding.
	* __Otsu's Method__: an automatic, optimal and noise-resistant thresholding.
* Contrast enhancement.
* Gamma correction, with a __γ__ parameter (_i.e._, the power).
* Regulable contamination:
	* __Gaussian Noise__: an additive noise of mean _µ_, and deviation _σ_.
	* __Exponencial Noise__: multiplicative noise, of parameter _λ_.
	* __Rayleigh Noise__: multiplicative noise, of parameter _ξ_.
	* __Salt & Pepper__: an injection of fully saturated pixels with some probability ratio.
* Filters, to restore or smooth a contaminated image:
	* __Gaussian__ of deviation _σ = 0.5 x (d-1)_, where _d_ is the dimension of the filter convolution matrix.
	* __Median__: great for restoring an image contaminated with _Salt & Pepper_ noise.
	* __Mean__: the most basic filter of the world.
	* __Weighted Mean__: only available for a dimension _d = 3_.
	* __Highpass__: for edges enhancing.
* Advanced filtering and diffusion, which makes easy an edge-detection process:
	* __Isotropic__.
	* __Anisotropic__.
	* __Bilinear__.
* Directional operators, the primitives of some edge-detectors:
	* __Prewitt__.
	* __Sobel__.
	* __Kirsh__.
* Edge-detectors:
	* __Gradient-based__.
	* __Laplacian-based__.
	* Laplacian with a customizable __slope__ _s_.
	* __Laplacian of Gaussian__, a.k.a. _The Marr-Hildreth Algorithm_.

## Build

To build the project, it is necessary to have _Maven +3.5.0_, and
_Java SE 10 Release_ installed. Then, run:

```
$ mvn clean package
```

This will generate a _\*.jar_ in the root folder. If you find any issues with
the building, remove the _\*.jar_ files from the _Maven_ local repository
with:

```
$ rm -fr ~/.m2/repository/ar/nadezhda/*
```

Or do it manually, if you prefer.

## Execution

In the root folder (after build), type:

```
$ java -jar target/refractor-1.0-SNAPSHOT.jar
```

## Designers

This project has been built, designed and maintained by:

* [Juan Pablo Dantur](https://github.com/jpdantur)
* [Agustín Golmar](https://github.com/agustin-golmar)

## Bibliography

__"Digital Image Processing"__. Rafael C. González, Richard E. Woods.
_Pearson. ISBN 978-0133356724. 2018_.
