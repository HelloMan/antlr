package ANTLR::Runtime::Exception;

use strict;
use warnings;

sub new {
    my ($class) = @_;

    my $self = bless {}, $class;

    return $self;
}

1;
